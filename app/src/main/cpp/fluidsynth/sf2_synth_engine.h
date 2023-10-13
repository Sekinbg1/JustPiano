/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class ly_pp_justpiano3_utils_Sf2SynthUtil */

#ifndef _Included_ly_pp_justpiano3_utils_Sf2SynthUtil
#define _Included_ly_pp_justpiano3_utils_Sf2SynthUtil
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     ly_pp_justpiano3_utils_Sf2SynthUtil
 * Method:    malloc
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_malloc
  (JNIEnv *, jobject);

/*
 * Class:     ly_pp_justpiano3_utils_Sf2SynthUtil
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_free
  (JNIEnv *, jobject, jlong);

/*
 * Class:     ly_pp_justpiano3_utils_Sf2SynthUtil
 * Method:    open
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_open
  (JNIEnv *, jobject, jlong);

/*
 * Class:     ly_pp_justpiano3_utils_Sf2SynthUtil
 * Method:    close
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_close
  (JNIEnv *, jobject, jlong);

/*
 * Class:     ly_pp_justpiano3_utils_Sf2SynthUtil
 * Method:    loadFont
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_loadFont
  (JNIEnv *, jobject, jlong, jstring);

/*
 * Class:     ly_pp_justpiano3_utils_Sf2SynthUtil
 * Method:    unloadFont
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_unloadFont
  (JNIEnv *, jobject, jlong);

/*
 * Class:     ly_pp_justpiano3_utils_Sf2SynthUtil
 * Method:    systemReset
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_systemReset
  (JNIEnv *, jobject, jlong);

/*
 * Class:     ly_pp_justpiano3_utils_Sf2SynthUtil
 * Method:    noteOn
 * Signature: (JIII)V
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_noteOn
  (JNIEnv *, jobject, jlong, jint, jint, jint);

/*
 * Class:     ly_pp_justpiano3_utils_Sf2SynthUtil
 * Method:    noteOff
 * Signature: (JIII)V
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_noteOff
  (JNIEnv *, jobject, jlong, jint, jint, jint);

/*
 * Class:     ly_pp_justpiano3_utils_Sf2SynthUtil
 * Method:    controlChange
 * Signature: (JIII)V
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_controlChange
  (JNIEnv *, jobject, jlong, jint, jint, jint);

/*
 * Class:     ly_pp_justpiano3_utils_Sf2SynthUtil
 * Method:    programChange
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_programChange
  (JNIEnv *, jobject, jlong, jint, jint);

/*
 * Class:     ly_pp_justpiano3_utils_Sf2SynthUtil
 * Method:    pitchBend
 * Signature: (JII)V
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_pitchBend
  (JNIEnv *, jobject, jlong, jint, jint);

/*
 * Class:     ly_pp_justpiano3_utils_Sf2SynthUtil
 * Method:    setDoubleProperty
 * Signature: (JLjava/lang/String;D)V
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_setDoubleProperty
  (JNIEnv *, jobject, jlong, jstring, jdouble);

/*
 * Class:     ly_pp_justpiano3_utils_Sf2SynthUtil
 * Method:    setIntegerProperty
 * Signature: (JLjava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_setIntegerProperty
  (JNIEnv *, jobject, jlong, jstring, jint);

/*
 * Class:     ly_pp_justpiano3_utils_Sf2SynthUtil
 * Method:    setStringProperty
 * Signature: (JLjava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_setStringProperty
  (JNIEnv *, jobject, jlong, jstring, jstring);

/*
 * Class:     ly_pp_justpiano3_utils_Sf2SynthUtil
 * Method:    getPropertyOptions
 * Signature: (JLjava/lang/String;Ljava/util/List;)V
 */
JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_getPropertyOptions
  (JNIEnv *, jobject, jlong, jstring, jobject);

JNIEXPORT void JNICALL Java_ly_pp_justpiano3_utils_Sf2SynthUtil_FillBuffer
(JNIEnv *, jobject, jlong, jshortArray, jint);

#ifdef __cplusplus
}
#endif
#endif
