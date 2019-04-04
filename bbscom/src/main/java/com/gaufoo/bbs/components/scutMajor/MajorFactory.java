package com.gaufoo.bbs.components.scutMajor;

import com.gaufoo.bbs.components.scutMajor.common.Major;
import com.gaufoo.bbs.components.scutMajor.common.MajorCode;
import com.gaufoo.bbs.components.scutMajor.common.MajorValue;
import com.gaufoo.bbs.components.scutMajor.common.School;

import java.util.Optional;
import java.util.stream.Stream;

public interface MajorFactory {
    Optional<MajorValue> getMajorValue(School school, Major major);

    Stream<Major> majorsIn(School school);

    MajorCode generateMajorCode(MajorValue majorValue);

    Optional<MajorValue> getMajorValueFromCode(MajorCode code);

    String getName();

    static MajorFactory defau1t(String componentName) {
        return new MajorFactoryImpl(componentName);
    }
}
