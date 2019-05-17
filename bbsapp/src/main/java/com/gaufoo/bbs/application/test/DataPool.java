package com.gaufoo.bbs.application.test;

import com.gaufoo.bbs.application.AppPersonalInformation;
import com.gaufoo.bbs.application.types.*;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataPool {

    private static Random random = new Random();
    private static long signUpCount = 0L;
    private static long lostItemCount = 0L;
    private static List<String> profiles = new ArrayList<>();
    private static List<String> postContents = new ArrayList<>();
    private static List<String> postTitles = new ArrayList<>();
    private static List<String> photos = new ArrayList<>();
    private static List<String> newsPhoto = new ArrayList<>();


    static {
        try {
            Path baseDir = Paths.get("../bbsapp").resolve("resources");

            profiles.addAll(Files.readAllLines(baseDir.resolve("profiles-bs64.txt")));
            postContents.addAll(Files.readAllLines(baseDir.resolve("ran-ctn.txt")));
            postTitles.addAll(Files.readAllLines(baseDir.resolve("ran-title.txt")));
            photos.addAll(Files.readAllLines(baseDir.resolve("photos-bs64.txt")));
            newsPhoto.addAll(Files.readAllLines(baseDir.resolve("news-pho.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static List<Authentication.SignupInput> generateSignUpInput(int number) {
        signUpCount += 1;
        List<Authentication.SignupInput> result = new LinkedList<>();
        for (int i = 0; i < number; ++i) {
            Authentication.SignupInput input = new Authentication.SignupInput();
            input.username = "user" + signUpCount + "@mail.scut.edu.cn";
            input.password = "password" + signUpCount;
            input.nickname = "nickname" + signUpCount;
            result.add(input);
        }
        return result;
    }

    public static String generateTitle() {
        return postTitles.get(random.nextInt(postTitles.size()));
    }

    private static List<String> genders = Stream.of("男", "女", "秘密", "其他").collect(Collectors.toList());
    private static List<String> grades = Stream.of("2018", "2017", "2016", "2015", "2014", "2013", "2012").collect(Collectors.toList());
    private static List<String> name = Stream.of("华工彭于晏", "吃饭不吃姜葱蒜", "正宗小鱼儿", "柠檬白兰地", "盘尼西林", "Timmy", "Sally", "Samsara", "MorningHaze", "Lycoris").collect(Collectors.toList());
    public static PersonalInformation.PersonInfoInput generatePersonInfo() {
        PersonalInformation.PersonInfoInput input = new PersonalInformation.PersonInfoInput();
        input.gender = genders.get(random.nextInt(genders.size()));
        input.grade = grades.get(random.nextInt(grades.size()));
//        input.major = AppPersonalInformation.allMajors().get(random.nextInt(AppPersonalInformation.allMajors().size()));
//        input.school = AppPersonalInformation.allSchools().get(random.nextInt(AppPersonalInformation.allSchools().size()));
        input.pictureBase64 = profiles.get(random.nextInt(profiles.size()));
        input.username = name.get(random.nextInt(name.size()));
        return input;
    }

    public static Content.ContentInput generateContentInput(int number) {
        Content.ContentInput input = new Content.ContentInput();
        input.elems = generateContentElemInput(random.nextInt(number) + 1);
        return input;
    }


    public static List<Content.ContentElemInput> generateContentElemInput(int number) {
        List<Content.ContentElemInput> inputs = new LinkedList<>();
        for (int i = 0; i < number; ++i) {
            Content.ContentElemInput contentElemInput = new Content.ContentElemInput();
            if (random.nextInt(100) == 0) {
                contentElemInput.type = Content.ElemType.Picture;
                contentElemInput.str = photos.get(random.nextInt(photos.size()));
            } else {
                contentElemInput.type = Content.ElemType.Text;
                contentElemInput.str = postContents.get(random.nextInt(postContents.size()));
            }
            inputs.add(contentElemInput);
        }
        return inputs;
    }

    private static List<String> locs = Stream.of("模拟法庭", "B3-213", "音乐厅", "B3-213", "A1-203", "A4-202", "A2-302", "A4-204").collect(Collectors.toList());
    private static List<Long> times = Stream.of(1512039600000L, 1509361200000L, 1527939000000L, 1545201000000L, 1542627000000L, 1538134200000L, 1542970800000L, 1529631000000L, 1523444400000L, 1544511600000L).collect(Collectors.toList());
    public static List<Lost.LostInput> generateLostInput(int number) {
        List<Lost.LostInput> inputs = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            Lost.LostInput input = new Lost.LostInput();
            input.contact = "1391234567";
            input.description = postContents.get(random.nextInt(postContents.size()));
            input.itemName = "item" + lostItemCount++;
            input.position = locs.get(random.nextInt(locs.size()));
            input.lostTime = times.get(random.nextInt(times.size()));
            input.pictureBase64 = photos.get(random.nextInt(photos.size()));
        }
        return inputs;
    }

    private static List<String> inames = Stream.of("饭卡", "水杯", "雨伞", "手机", "钥匙", "女朋友", "课本").collect(Collectors.toList());
    public static List<Found.FoundInput> generateFoundInput(int number) {
        List<Found.FoundInput> inputs = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            Found.FoundInput input = new Found.FoundInput();
            input.contact = "1391234567";
            input.description = postContents.get(random.nextInt(postContents.size()));
            input.itemName = inames.get(random.nextInt(inames.size()));
            input.position = locs.get(random.nextInt(locs.size()));
            input.foundTime = times.get(random.nextInt(times.size()));
            input.pictureBase64 = photos.get(random.nextInt(photos.size()));
            inputs.add(input);
        }
        return inputs;
    }

    private static Lecture.LectureInput clec(String title, String name, String loc, Long time) {
        Lecture.LectureInput input = new Lecture.LectureInput();
        input.lecturer = name;
        input.note = postContents.get(random.nextInt(postContents.size()));
        input.position = loc;
        input.time = time;
        input.title = title;
        Content.ContentInput cip = new Content.ContentInput();
        Content.ContentElemInput contentElemInput = new Content.ContentElemInput();
        contentElemInput.type = Content.ElemType.Text;
        contentElemInput.str = postContents.get(random.nextInt(postContents.size()));
        cip.elems = Collections.singletonList(contentElemInput);
        input.content = cip;
        return input;
    }
    private static List<Lecture.LectureInput> lectures = Stream.of(
            clec("不忘初心，不负新时代", "吴顺民董事长", "模拟法庭", 1512039600000L),
            clec("城市数据可视化", "阿里云", "B3-213", 1509361200000L),
            clec("“音为友你”班级音乐会", "15级音乐表演", "音乐厅", 1527939000000L),
            clec("Microsoft创新杯", "香江集团", "B3-213", 1545201000000L),
            clec("拥抱智能，对话未来", "微软俱乐部", "A1-203", 1542627000000L),
            clec("网络安全漫谈", "何军辉教授", "A4-202", 1538134200000L),
            clec("百度高校技术会", "李轩涯博士", "模拟法庭", 1542970800000L),
            clec("因果建模与机器学习", "Prof.KunZhang", "A2-302", 1529631000000L),
            clec("微信小程序大赛发布会", "荔枝微课", "音乐厅", 1523444400000L),
            clec("音频领域的机遇和挑战", "方老师", "A4-204", 1544511600000L)
    ).collect(Collectors.toList());
    public static List<Lecture.LectureInput> generateLectureInput(int number) {
        List<Lecture.LectureInput> inputs = new LinkedList<>();
        for (int i = 0; i < number; i++) {
            inputs.add(lectures.get(random.nextInt(lectures.size())));
        }
        return inputs;
    }

    private static News.NewsInput consNIp(String title, List<String> content, String base64) {
        News.NewsInput input = new News.NewsInput();
        input.title = title;

        Content.ContentInput cip = new Content.ContentInput();
        cip.elems = content.stream().map(i -> {
            Content.ContentElemInput contentElemInput = new Content.ContentElemInput();
            contentElemInput.type = Content.ElemType.Text;
            contentElemInput.str = i;
            return contentElemInput;
        }).collect(Collectors.toList());

        input.content = cip;

        input.pictureBase64 = base64;
        return input;
    }

    public static List<News.NewsInput> newsInput = Stream.of(
            consNIp("留学生志愿者小分队：我们用爱与双手与中国交流对话", Stream.of(
                    "5月15日上午，亚洲文明对话大会在北京国家会议中心隆重开幕。国家主席习近平出席开幕式并发表主旨演讲。习近平在讲话中提出四点主张，第一，坚持相互尊重、平等相待。第二，坚持美人之美、美美与共。第三，坚持开放包容、互学互鉴。第四，坚持与时俱进、创新发展。",
                    "在华南理工大学留学生中，有一群热心公益事业的年轻人，他们来自不同国家却有着同样的志愿者精神，他们用爱与双手与中国交流对话。",
                    "他们就是“留学生志愿者小分队”。这个组织成立于2010年。每年有来自61个国家的200余名留学生参加志愿服务活动，其中来自印度尼西亚、泰国、哈萨克斯坦等37个亚洲国家的留学生人数有100人次，约占50%。",
                    "在学院组织下，他们不断完善服务组织，开拓服务项目，努力把志愿者服务活动与德育相结合，把志愿者服务与技能训练相结合，围绕“爱幼、扶贫、助残、助学、环保、社区服务和公益宣传”开展了形式多样的志愿服务活动。",
                    "他们每年参与和组织留学生献血超过200人次；经常组织留学生前往福利院探望残疾儿童，为小朋友们带去礼物、讲故事、做游戏，把希望和快乐带给这些不幸的儿童；他们乐善好施、饱含真心，多次组织留学生捐献衣物给中国贫困地区；他们扶危济困，组织老生捐献旧书留给新来的学生，还在有的同学遇到重大困难时，积极组织捐款；此外，他们还组织假期留校的留学生参加广州春运志愿者活动等，成为无数中国人回家路上温暖的一道风景。",
                    "留学生志愿者小分队自建立以来充分发挥青年志愿者“自我管理”“自我教育”“自我服务”的作用，丰富多样的无私奉献精神的活动在社会上引起良好的反响。他们在无偿付出的同时也为其他留学生提供了一个奉献爱心的平台，一个表达爱的机会。他们不是中国人，但愿意为中国做出自己的贡献，因为中国就是他们的第二故乡。（图文/国际教育学院 周娟 创意设计/设计学院 王慧 莫志豪 伍家豪 编辑/周玉）").collect(Collectors.toList()),
                    newsPhoto.get(0)),
            consNIp("大型多媒体全景式话剧《红色甲工 血色浪漫》", Stream.of(
                    "血与火，是至烈燃烧的绚烂玫瑰。",
                    "爱与情，是浪漫纯真的青春岁月。",
                    "90年前的华工，有这样一群学生，他们在群敌环伺间，用高昂的气骨，以卓绝的身姿，谱下了与家、与国的血色动人篇章。有这样一群学生，他们在生命结束前，用气吞山河的呐喊，以心中的澎湃，绘出了激情燃烧的青春岁月。",
                    "“头可断，肢可折，革命精神不可灭。壮士头颅为党落，好汉身躯为群裂！”",
                    "1927年，为了反抗“四一五反革命”，广东省立第一甲种工业学校（华工最早的办学源头）涌现出一批英雄人物带领工农大众武装起义，其中的主要领导者就有周文雍。",
                    "为了不让敌人起疑，周文雍与陈铁军在革命中，如何假扮夫妻与敌人斗智斗勇？",
                    "面对反动派的威逼利诱，他们是如何相互掩护、默契配合的？",
                    "在家国危难面前，他们又将如何取舍？",
                    "在生命的最后时刻，他们用什么来捍卫自己的革命事业与爱情？",
                    "5月10日晚上7点半，大学城校区学术大讲堂，华南理工大学的年轻学子们将会再次演绎先烈校友的英雄事迹与动人的情感故事。让我们一起相约大型多媒体全景式话剧《红色甲工 血色浪漫》的剧场，共同体验九十年前的壮烈与浪漫！（图文/校团委 苏香轩 创意设计/设计学院 罗晓燕 胡婉清 莫志豪 编辑/周玉）").collect(Collectors.toList()),
                    newsPhoto.get(1)),
            consNIp("华南理工大学纪念五四运动100周年", Stream.of(
                    "中共中央总书记、国家主席、中央军委主席习近平在纪念五四运动100周年大会上发表重要讲话强调，五四运动以来的100年，是中国青年一代又一代接续奋斗、凯歌前行的100年，是中国青年用青春之我创造青春之中国、青春之民族的100年。新时代中国青年运动的主题，新时代中国青年运动的方向，新时代中国青年的使命，就是坚持中国共产党领导，同人民一道，为实现“两个一百年”奋斗目标、实现中华民族伟大复兴的中国梦而奋斗。",
                    "五四运动是我国近现代史上具有里程碑意义的重大事件，五四精神是五四运动创造的宝贵精神财富。华南理工大学认真学习贯彻落实习近平总书记关于加强对五四运动和五四精神研究的重要讲话精神，举办纪念五四运动100周年系列主题活动。包括纪念五四运动100周年表彰晚会，纪念五四运动100周年主题团日暨升国旗仪式，“与信仰对话——新时代国旗文化的使命”主题报告会，《星星之火》《红色甲工，血色浪漫》话剧公演等。（图文/校团委 创意设计/设计学院 胡婉清 莫志豪 编辑/李伟群）").collect(Collectors.toList()),
                    newsPhoto.get(2)),
            consNIp("芳菲四月，悦读华园", Stream.of(
                    "每年的4月23日被定为“世界读书日”，“世界读书日”是世界文学的象征日，设立目的是希望每个人都能热爱读书，能尊重和感谢为人类文明做出过巨大贡献的文学、文化、科学、思想大师们，都能保护知识产权。",
                    "在世界读书日来临之际，华南理工大学举办了“芳菲四月，悦读华园”读书节文化活动，让全校师生在飘满温情书香的华园里尽情享受阅读的乐趣。活动包括：",
                    "一、“悠悠书香悦读时光”读者书评大赛；",
                    "二、华园求真书会：“声”扬强国音，诗书家国情——吴硕贤院士畅谈“科技改善人居声环境”；",
                    "三、启真讲坛：刘焕彬院士开讲《创新之源——把知识转化为智慧》；",
                    "四、2019年“世界读书日”中外文图书展；",
                    "五、“让知识触手可及”——万方数据知识服务平台使用培训讲座；",
                    "六、图书馆杯全民英语口语风采展示大赛。",
                    "（图/李泽其 赵岚 文/图书馆 创意设计/李冠樱 编辑/李伟群）").collect(Collectors.toList()),
                    newsPhoto.get(3)),
            consNIp("张宪民教授团队产学研结合助推晶硅光伏太阳能领域快速发展", Stream.of(
                    "跨入21世纪，人类面临实现经济和社会可持续发展的重大挑战，现代工业的发展，一方面加大对能源的需求，引发能源危机；另一方面在常规能源的开发利用过程中带来了一系列环境问题。太阳能作为最理想的可再生能源，具有储量的“无限性”、存在的普遍性、利用的清洁性等特点，因此世界各国都在大力推进太阳能的开发利用。以光伏太阳能电池为核心的太阳能电力系统已受到全球范围内的高度重视。",
                    "由于基于丝网印刷技术的太阳能电池生产具有设备可靠、生产效率高等优点，已经成为了主流的低成本量产技术。目前，全球大约有86%的硅基太阳能电池是采用丝网印刷技术路线制造的。在这样的背景下，华南理工大学张宪民教授领导的研究团队与东莞市科隆威自动化设备有限公司产学研合作，对基于丝网印刷的晶硅太阳能电池制造关键技术进行了系统深入研究，在此基础上研制了相应的成套装备。成果“基于丝网印刷的晶硅光伏太阳能电池关键技术及成套装备”荣获2018年度广东省科学技术奖技术发明一等奖。",
                    "成果针对基于丝网印刷技术的高效晶硅太阳能光伏电池生产线成套设备及关键技术进行研究，并形成自动化生产线，主要成套设备包括：精密视觉丝网印刷系统、光衰系统、烘干烧结系统、太阳能电池智能检测分拣系统等。在此基础上形成具有自我监测、诊断能力的自动化生产线。",
                    "项目针对基于丝网印刷的晶硅光伏太阳能电池关键技术及成套装备进行了深入系统研究，在整机技术方面发明了晶硅光伏太阳能电池印刷机，烘干烧结系统、光衰系统、分拣系统等关键装备整机；在共性技术上，发明了视觉精密定位系统与标定方法，实现了印刷过程的快速、精确定位；在单元关键技术方面发明了双线印刷移栽装置、印刷机调网机构、晶硅太阳能电池片双线丝印烧结工艺等。",
                    "项目授权美国、日本等发达国家发明专利6项、授权国内发明专利46项，授权软件著作权6项，发表论文83篇。成套设备已成功产业化，项目产品2012年投入市场以来，已在30多个著名企业得到应用，取得了很好的经济效益。",
                    "该成套设备生产的晶硅光伏太阳能电池已广泛用于太阳能光伏农业大棚、LED路灯、国内外大型电站等重大工程中。该成果提高了我国晶硅光伏太阳能电池生产装备的整体技术水平和市场竞争力，关键技术形成了专利群，有效推动了行业的科技进步，社会效益显著。（文/机械与汽车工程学院 图/田若妍 邓泽深 创意设计/设计学院 罗晓燕 胡婉清 王慧 编辑/李伟群）").collect(Collectors.toList()),
                    newsPhoto.get(4))
            ).collect(Collectors.toList());

    public static void main(String[] args) {
        System.out.println("hello world!" + postTitles.size());
    }
}
