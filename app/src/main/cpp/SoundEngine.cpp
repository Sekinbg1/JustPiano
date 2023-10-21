#include <jni.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include "fluidsynth.h"

#include <android/log.h>

// parselib includes
#include <stream/MemInputStream.h>
#include <wav/WavStreamReader.h>
#include "utils/Utils.h"

#include <player/OneShotSampleSource.h>
#include <player/SimpleMultiPlayer.h>

static const char *TAG = "SoundEngine";

// JNI functions are "C" calling convention
#ifdef __cplusplus
extern "C" {
#endif

using namespace iolib;
using namespace parselib;

static SimpleMultiPlayer sDTPlayer;

typedef struct {
    fluid_settings_t *settings;
    fluid_synth_t *synth;
    int soundfont_id;
} fluid_handle_t;

JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_SoundEngineUtil_setupAudioStreamNative(
        JNIEnv *env, jclass, jint numChannels, jint sampleRate) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "%s", "init()");

    // we know in this case that the sample buffers are all 1-channel, 41K
    sDTPlayer.setupAudioStream(numChannels, sampleRate);
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_SoundEngineUtil_teardownAudioStreamNative(JNIEnv *, jclass) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "%s", "deinit()");

    // we know in this case that the sample buffers are all 1-channel, 44.1K
    sDTPlayer.teardownAudioStream();
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_SoundEngineUtil_loadWavAssetNative(JNIEnv *env, jclass,
                                                               jbyteArray bytearray,
                                                               jint index, jfloat pan) {
    int len = env->GetArrayLength(bytearray);

    auto *buf = new unsigned char[len];
    env->GetByteArrayRegion(bytearray, 0, len, reinterpret_cast<jbyte *>(buf));

    MemInputStream stream(buf, len);

    WavStreamReader reader(&stream);
    reader.parse();

    auto *sampleBuffer = new SampleBuffer();
    sampleBuffer->loadSampleData(&reader);

    auto *source = new OneShotSampleSource(sampleBuffer);
    sDTPlayer.addSampleSource(source, sampleBuffer);

    delete[] buf;
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_SoundEngineUtil_unloadWavAssetsNative(JNIEnv *env, jclass) {
    sDTPlayer.unloadSampleData();
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_SoundEngineUtil_trigger(JNIEnv *env, jclass, jint index, jint volume) {
    sDTPlayer.triggerDown(index, volume);
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_SoundEngineUtil_clearOutputReset(JNIEnv *, jclass) {
    sDTPlayer.clearOutputReset();
}

JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_SoundEngineUtil_restartStream(JNIEnv *, jclass) {
    sDTPlayer.resetAll();
    if (sDTPlayer.openStream()) {
        __android_log_print(ANDROID_LOG_INFO, TAG, "openStream successful");
    } else {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "openStream failed");
    }
}

JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_SoundEngineUtil_setRecord(
        JNIEnv *env, jclass thiz, jboolean record) {
    sDTPlayer.setRecord(record);
}

JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_SoundEngineUtil_setRecordFilePath(
        JNIEnv *env, jclass thiz, jstring recordFilePath) {
    char *path = java_str_to_c_str(env, recordFilePath);
    sDTPlayer.setRecordFilePath(path);
}

JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_SoundEngineUtil_setSf2SynthPtr(
        JNIEnv *env, jclass thiz, jlong ptr) {
    fluid_handle_t *handle = nullptr;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != nullptr && handle->synth != nullptr) {
        sDTPlayer.setSf2SynthPtr(handle->synth);
    } else {
        sDTPlayer.setSf2SynthPtr(nullptr);
    }
}

JNIEXPORT jlong JNICALL
Java_ly_pp_justpiano3_utils_SoundEngineUtil_malloc(JNIEnv *env, jclass obj) {
    jlong ptr = 0;

    auto *handle = (fluid_handle_t *) malloc(sizeof(fluid_handle_t));

    handle->settings = new_fluid_settings();
    handle->synth = nullptr;
    handle->soundfont_id = 0;

    fluid_settings_setint(handle->settings, const_cast<char *>("synth.polyphony"), 4096);
    fluid_settings_setstr(handle->settings, const_cast<char *>("audio.sample-format"), const_cast<char *>("float"));
    fluid_settings_setnum(handle->settings, const_cast<char *>("synth.gain"), 0.5f);

    memcpy(&ptr, &handle, sizeof(handle));
    return ptr;
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_SoundEngineUtil_free(JNIEnv *env, jclass obj, jlong ptr) {
    fluid_handle_t *handle = nullptr;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != nullptr) {
        if (handle->synth != nullptr) {
            delete_fluid_synth(handle->synth);
        }
        if (handle->settings != nullptr) {
            delete_fluid_settings(handle->settings);
        }
        free(handle);
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_SoundEngineUtil_open(JNIEnv *env, jclass obj, jlong ptr) {
    fluid_handle_t *handle = nullptr;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != nullptr && handle->settings != nullptr) {
        if (handle->synth != nullptr) {
            delete_fluid_synth(handle->synth);
        }
        handle->synth = new_fluid_synth(handle->settings);

        fluid_synth_set_interp_method(handle->synth, -1, FLUID_INTERP_NONE);
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_SoundEngineUtil_close(JNIEnv *env, jclass obj, jlong ptr) {
    fluid_handle_t *handle = nullptr;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != nullptr) {
        if (handle->synth != nullptr) {
            delete_fluid_synth(handle->synth);
        }
        handle->synth = nullptr;
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_SoundEngineUtil_loadFont(JNIEnv *env, jclass obj, jlong ptr,
                                                     jstring filePath) {
    fluid_handle_t *handle = nullptr;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != nullptr && handle->synth != nullptr && handle->soundfont_id <= 0) {
        char *path = java_str_to_c_str(env, filePath);
        handle->soundfont_id = fluid_synth_sfload(handle->synth, path, 1);
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_SoundEngineUtil_unloadFont(JNIEnv *env, jclass obj, jlong ptr) {
    fluid_handle_t *handle = nullptr;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != nullptr && handle->synth != nullptr && handle->soundfont_id > 0) {
        fluid_synth_sfunload(handle->synth, handle->soundfont_id, 1);
        handle->soundfont_id = 0;
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_SoundEngineUtil_noteOn(JNIEnv *env, jclass obj, jlong ptr, jint channel,
                                                   jint note, jint velocity) {
    fluid_handle_t *handle = nullptr;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != nullptr && handle->synth != nullptr) {
        fluid_synth_noteon(handle->synth, channel, note, velocity);
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_SoundEngineUtil_noteOff(JNIEnv *env, jclass obj, jlong ptr,
                                                    jint channel,
                                                    jint note) {
    fluid_handle_t *handle = nullptr;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != nullptr && handle->synth != nullptr) {
        fluid_synth_noteoff(handle->synth, channel, note);
    }
}

JNIEXPORT void JNICALL
Java_ly_pp_justpiano3_utils_SoundEngineUtil_controlChange(JNIEnv *env, jclass obj, jlong ptr,
                                                          jint channel, jint control, jint value) {
    fluid_handle_t *handle = nullptr;
    memcpy(&handle, &ptr, sizeof(handle));
    if (handle != nullptr && handle->synth != nullptr) {
        fluid_synth_cc(handle->synth, channel, control, value);
    }
}

#ifdef __cplusplus
}
#endif
