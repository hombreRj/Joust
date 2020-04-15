package gg.scenarios.joust.managers.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
public enum KitType {
    BUILD("Build UHC"),
    UHC("UHC"),
    IRON("Iron"),
    UHCPLUS("UHC +");

    @Getter
    private String displayName;

}
