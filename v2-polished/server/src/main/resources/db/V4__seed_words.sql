-- 初中英语 (book_id=1)
INSERT INTO `word` (`word_book_id`, `english`, `chinese`) VALUES
(1,'apple','苹果'),(1,'book','书'),(1,'cat','猫'),(1,'dog','狗'),(1,'elephant','大象'),
(1,'family','家庭'),(1,'garden','花园'),(1,'happy','快乐的'),(1,'interesting','有趣的'),
(1,'jump','跳'),(1,'kitchen','厨房'),(1,'library','图书馆'),(1,'morning','早晨'),
(1,'number','数字'),(1,'orange','橙子'),(1,'people','人们'),(1,'quiet','安静的'),
(1,'river','河流'),(1,'school','学校'),(1,'teacher','老师'),(1,'umbrella','雨伞'),
(1,'village','村庄'),(1,'water','水'),(1,'yellow','黄色的'),(1,'zero','零');

-- 高中英语 (book_id=2)
INSERT INTO `word` (`word_book_id`, `english`, `chinese`) VALUES
(2,'abandon','放弃'),(2,'benefit','利益；受益'),(2,'challenge','挑战'),(2,'debate','辩论'),
(2,'economy','经济'),(2,'fluent','流利的'),(2,'generate','产生'),(2,'hesitate','犹豫'),
(2,'ignore','忽视'),(2,'journey','旅程'),(2,'knowledge','知识'),(2,'lecture','讲座'),
(2,'measure','测量；措施'),(2,'negative','消极的'),(2,'observe','观察'),
(2,'phenomenon','现象'),(2,'quality','质量'),(2,'recognize','认出；承认'),
(2,'schedule','时间表'),(2,'tradition','传统'),(2,'unique','独特的'),
(2,'volunteer','志愿者'),(2,'witness','目击；证人'),(2,'youth','青春'),(2,'zone','区域');

-- 四级 (book_id=3)
INSERT INTO `word` (`word_book_id`, `english`, `chinese`) VALUES
(3,'academic','学术的'),(3,'budget','预算'),(3,'circumstance','环境；情况'),
(3,'decline','下降；拒绝'),(3,'effective','有效的'),(3,'flexible','灵活的'),
(3,'guarantee','保证'),(3,'highlight','强调'),(3,'identical','完全相同的'),
(3,'justify','证明…正当'),(3,'keen','敏锐的；热衷的'),(3,'launch','启动；发射'),
(3,'maintain','维持'),(3,'negotiate','谈判'),(3,'obstacle','障碍'),
(3,'perspective','观点；视角'),(3,'quote','引用'),(3,'release','释放；发布'),
(3,'strategy','策略'),(3,'temporary','暂时的'),(3,'undergo','经历'),
(3,'valid','有效的'),(3,'welfare','福利'),(3,'yield','产出；屈服'),(3,'absolute','绝对的');

-- 六级 (book_id=4)
INSERT INTO `word` (`word_book_id`, `english`, `chinese`) VALUES
(4,'absurd','荒谬的'),(4,'barren','贫瘠的'),(4,'chronic','慢性的'),
(4,'deteriorate','恶化'),(4,'elaborate','精心制作的；详细说明'),(4,'fragile','脆弱的'),
(4,'gloomy','阴沉的；沮丧的'),(4,'handicap','障碍；残疾'),(4,'illuminate','照亮；阐明'),
(4,'jeopardize','危及'),(4,'knit','编织；紧密结合'),(4,'linger','徘徊'),
(4,'magnificent','壮丽的'),(4,'notorious','臭名昭著的'),(4,'overthrow','推翻'),
(4,'perish','死亡；毁灭'),(4,'quench','熄灭；解渴'),(4,'reckless','鲁莽的'),
(4,'stereotype','刻板印象'),(4,'thrive','繁荣'),(4,'utilize','利用'),
(4,'vulgar','粗俗的'),(4,'weary','疲倦的'),(4,'zeal','热情');

-- 考研 (book_id=5)
INSERT INTO `word` (`word_book_id`, `english`, `chinese`) VALUES
(5,'abolish','废除'),(5,'bias','偏见'),(5,'compel','强迫'),(5,'discourse','话语'),
(5,'emerge','出现'),(5,'fluctuate','波动'),(5,'grieve','悲伤'),(5,'hinder','阻碍'),
(5,'integrate','整合'),(5,'jury','陪审团'),(5,'legislation','立法'),
(5,'monopoly','垄断'),(5,'narrative','叙述'),(5,'oppress','压迫'),(5,'paradox','悖论'),
(5,'rigorous','严谨的'),(5,'scrutiny','仔细审查'),(5,'testimony','证词'),
(5,'undermine','削弱'),(5,'verdict','裁决');

-- 托福 (book_id=6)
INSERT INTO `word` (`word_book_id`, `english`, `chinese`) VALUES
(6,'abundant','丰富的'),(6,'biodiversity','生物多样性'),(6,'collaborate','合作'),
(6,'drought','干旱'),(6,'erosion','侵蚀'),(6,'famine','饥荒'),(6,'habitat','栖息地'),
(6,'indigenous','本土的'),(6,'lunar','月球的'),(6,'metabolism','新陈代谢'),
(6,'orbit','轨道'),(6,'plateau','高原'),(6,'reservoir','水库'),(6,'sediment','沉积物'),
(6,'tectonic','地壳构造的'),(6,'unprecedented','前所未有的'),
(6,'vegetation','植被'),(6,'watershed','分水岭'),(6,'photosynthesis','光合作用'),
(6,'sustainability','可持续性');

-- SAT (book_id=7)
INSERT INTO `word` (`word_book_id`, `english`, `chinese`) VALUES
(7,'ambivalent','矛盾的'),(7,'benevolent','仁慈的'),(7,'candor','坦率'),
(7,'diligent','勤奋的'),(7,'eloquent','雄辩的'),(7,'frugal','节俭的'),
(7,'gregarious','爱社交的'),(7,'hackneyed','陈腐的'),(7,'innate','天生的'),
(7,'juxtapose','并列'),(7,'laconic','简洁的'),(7,'mundane','平凡的'),
(7,'nonchalant','漠不关心的'),(7,'ostentatious','炫耀的'),(7,'pragmatic','务实的'),
(7,'reticent','沉默寡言的'),(7,'sagacious','睿智的'),(7,'tenacious','顽强的'),
(7,'verbose','冗长的'),(7,'whimsical','异想天开的');

-- 更新词库单词计数
UPDATE word_book wb SET word_count = (SELECT COUNT(*) FROM word WHERE word_book_id = wb.id);
