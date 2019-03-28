package com.gaufoo.bbs.components.scutMajor;

import com.gaufoo.bbs.components.scutMajor.common.Major;
import com.gaufoo.bbs.components.scutMajor.common.MajorCode;
import com.gaufoo.bbs.components.scutMajor.common.MajorValue;
import com.gaufoo.bbs.components.scutMajor.common.School;
import com.gaufoo.bbs.util.Util;

import java.util.*;
import java.util.stream.Stream;

public class MajorFactoryImpl implements MajorFactory {
    private final String componentName;
    private static final Map<School, List<Major>> schoolToMajor = new HashMap<>();

    static  {
        schoolToMajor.put(School.无, Util.buildList(Major.无));
    }

    private MajorFactoryImpl(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public Optional<MajorValue> getMajorValue(School school, Major major) {
        if (schoolToMajor.get(school).contains(major))
            return Optional.of(MajorValue.of(school, major));
        return Optional.empty();
    }

    @Override
    public Stream<Major> majorsIn(School school) {
        return schoolToMajor.get(school).stream();
    }

    @Override
    public MajorCode generateMajorCode(MajorValue majorValue) {
        String s = String.format("%03d", majorValue.school.ordinal());
        String code = s + String.format("%03d", majorValue.major.ordinal());
        return MajorCode.of(code);
    }

    @Override
    public Optional<MajorValue> getMajorValueFromCode(MajorCode code) {
        if (code.value.length() != 6) return Optional.empty();
        if (!code.value.matches("\\d*")) return Optional.empty();
        int school = Integer.parseInt(code.value.substring(3));
        int major = Integer.parseInt(code.value.substring(3, 6));
        if (school >= School.values().length || major >= Major.values().length) return Optional.empty();
        return getMajorValue(School.values()[school], Major.values()[major]);
    }

    @Override
    public String getName() {
        return this.componentName;
    }

    public static void main(String[] args) {
        MajorFactoryImpl mf = new MajorFactoryImpl("");
        Optional<MajorValue> m = mf.getMajorValue(School.无, Major.无);

        System.out.println(m.get());

        MajorCode code = mf.generateMajorCode(m.get());

        System.out.println(code);

        System.out.println(mf.getMajorValueFromCode(code));
    }
}