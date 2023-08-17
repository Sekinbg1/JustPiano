package ly.pp.justpiano3.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 游戏模式枚举
 *
 * @author as
 **/
@Getter
@AllArgsConstructor
public enum GameModeEnum {

    NORMAL(0, "普通模式"),
    FREESTYLE(1, "自由模式"),
    PRACTISE(2, "练习模式"),
    HEAR(3, "欣赏模式"),
    ;

    private final int code;
    private final String desc;
}
