package com.philips.project.msdb.beans;

import java.util.Random;

public enum AreaEnum {
        North,
        South,
        Central;

        public static AreaEnum generateRandomArea() {
                AreaEnum[] values = AreaEnum.values();
                int length = values.length;
                int randIndex = new Random().nextInt(length);
                return values[randIndex];
        }
}
