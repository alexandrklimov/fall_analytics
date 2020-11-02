package org.aklimov.fall_analytics.lib.services.domain

enum class ValidPercentilesEnum {
    P_1, P_2, P_3, P_4, P_5, P_6, P_7, P_8, P_9, P_10,
    P_11, P_12, P_13, P_14, P_15, P_16, P_17, P_18, P_19, P_20,
    P_21, P_22, P_23, P_24, P_25, P_26, P_27, P_28, P_29, P_30,
    P_31, P_32, P_33, P_34, P_35, P_36, P_37, P_38, P_39, P_40,
    P_41, P_42, P_43, P_44, P_45, P_46, P_47, P_48, P_49, P_50,
    P_51, P_52, P_53, P_54, P_55, P_56, P_57, P_58, P_59, P_60,
    P_61, P_62, P_63, P_64, P_65, P_66, P_67, P_68, P_69, P_70,
    P_71, P_72, P_73, P_74, P_75, P_76, P_77, P_78, P_79, P_80,
    P_81, P_82, P_83, P_84, P_85, P_86, P_87, P_88, P_89, P_90,
    P_91, P_92, P_93, P_94, P_95, P_96, P_97, P_98, P_99, P_100;

    val value: Int
        get() = this.name.replace("P_","").toInt()


    companion object{
        /**
         * Ordered list _(ASC)_ of valid percentiles
         */
        val all: List<ValidPercentilesEnum>
            get() = ValidPercentilesEnum.values().sortedBy(ValidPercentilesEnum::value)
    }

}
