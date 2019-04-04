package com.gaufoo.bbs.components.scutMajor;

import com.gaufoo.bbs.components.scutMajor.common.Major;
import com.gaufoo.bbs.components.scutMajor.common.MajorCode;
import com.gaufoo.bbs.components.scutMajor.common.MajorValue;
import com.gaufoo.bbs.components.scutMajor.common.School;
import com.gaufoo.bbs.util.Util;

import java.util.*;
import java.util.stream.Stream;

import static com.gaufoo.bbs.components.scutMajor.common.Major.*;
import static com.gaufoo.bbs.components.scutMajor.common.School.*;

public class MajorFactoryImpl implements MajorFactory {
    private final String componentName;
    private static final Map<School, List<Major>> schoolToMajor = new HashMap<>();

    static {
        schoolToMajor.put(School.无, Util.buildList(Major.无));
        schoolToMajor.put(艺术学院, Util.buildList(音乐表演, 舞蹈学, 音乐学));
        schoolToMajor.put(设计学院, Util.buildList(工业设计, 环境设计, 产品设计, 服装与服饰设计));
        schoolToMajor.put(法学院, Util.buildList(法学, 知识产权));
        schoolToMajor.put(经济与贸易学院, Util.buildList(国际经济与贸易, 金融学, 物流工程, 电子商务, 旅游管理, 会展经济与管理, 经济学));
        schoolToMajor.put(医学院, Util.buildList(医学影像学, 医学影像技术, 生物信息学));
        schoolToMajor.put(轻工科学与工程学院, Util.buildList(轻化工程, 资源环境科学));
        schoolToMajor.put(食品科学与工程学院, Util.buildList(食品科学与工程, 食品质量与安全));
        schoolToMajor.put(物理与光电学院, Util.buildList(应用物理学, 光电信息科学与工程));
        schoolToMajor.put(数学学院, Util.buildList(数学与应用数学, 统计学, 信息管理与信息系统, 信息与计算科学));
        schoolToMajor.put(工商管理学院, Util.buildList(工商管理, 财务管理, 人力资源管理, 市场营销, 会计学));
        schoolToMajor.put(公共管理学院, Util.buildList(行政管理, 公共政策, 社会保障, 土地资源管理));
        schoolToMajor.put(外国语学院, Util.buildList(商务英语, 日语));
        schoolToMajor.put(计算机科学与工程学院, Util.buildList(计算机科学与技术, 网络工程, 信息安全));
        schoolToMajor.put(软件学院, Util.buildList(软件工程));
        schoolToMajor.put(环境与能源学院, Util.buildList(环境科学, 环境工程, 给排水科学与工程));
        schoolToMajor.put(生物科学与工程学院, Util.buildList(生物工程, 生物技术, 生物制药));
        schoolToMajor.put(新闻与传播学院, Util.buildList(新闻学, 传播学, 广告学));
        schoolToMajor.put(体育学院, Util.buildList(运动训练));
        schoolToMajor.put(土木与交通学院, Util.buildList(土木工程, 工程管理, 交通工程, 交通运输, 工程力学, 船舶与海洋工程, 水利与水电工程));
        schoolToMajor.put(电力学院, Util.buildList(电气工程及其自动化, 能源与动力工程, 核工程与核技术));
        schoolToMajor.put(电子与信息学院, Util.buildList(信息工程, 电子科学与技术));
        schoolToMajor.put(自动化科学与工程学院, Util.buildList(自动化, 智能科学与技术));
        schoolToMajor.put(材料科学与工程学院, Util.buildList(高分子材料与工程, 材料化学, 材料科学与工程, 电子科学与技术, 光电信息科学与工程, 生物医学工程));
        schoolToMajor.put(化学与化工学院, Util.buildList(化学工程与工艺, 应用化学, 制药工程, 能源化学工程));
        schoolToMajor.put(机械与汽车工程学院, Util.buildList(机械工程, 机械电子工程, 过程装备与控制工程, 安全工程, 材料成型及控制工程, 车辆工程));
        schoolToMajor.put(建筑学院, Util.buildList(建筑学, 城乡规划, 风景园林));
    }

    MajorFactoryImpl(String componentName) {
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
        int school = Integer.parseInt(code.value.substring(0, 3));
        int major = Integer.parseInt(code.value.substring(3));
        if (school >= School.values().length || major >= Major.values().length) return Optional.empty();
        return getMajorValue(School.values()[school], Major.values()[major]);
    }

    @Override
    public String getName() {
        return this.componentName;
    }

    public static void main(String[] args) {
        MajorFactoryImpl mf = new MajorFactoryImpl("");
        Optional<MajorValue> m = mf.getMajorValue(计算机科学与工程学院, 计算机科学与技术);

        System.out.println(m.get());

        MajorCode code = mf.generateMajorCode(m.get());

        System.out.println(code);

        System.out.println(mf.getMajorValueFromCode(code));
    }
}

