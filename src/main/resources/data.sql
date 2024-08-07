-- 유저 더미 데이터 (비밀번호는 'Password1234~!')
insert into users (email, name, password, phone, deleted_at, created_at, updated_at)
values ('test@test.com', 'test', '$2a$10$v.DHYimH.eimSYeMF2vtdeOHgbu3JElaN.OPBzaYyQzlwrwbBeVD2',
        '010-0000-0000', '1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('user@test.com', 'user', '$2a$10$v.DHYimH.eimSYeMF2vtdeOHgbu3JElaN.OPBzaYyQzlwrwbBeVD2',
        '010-1234-1234', '1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
insert into user_roles (user_email, roles)
values ('test@test.com', 'ADMIN'),
       ('test@test.com', 'USER'),
       ('user@test.com', 'USER');

-- 게시글 더미 데이터
-- @SoftDelete에서 NULL을 허용하지 않기 때문에, 삭제 되지 않음을 표현하기 위해서 TimeStamp(0)값을 넣어줌
insert into article (deleted_at, updated_at, created_at, title, content, author_email)
values ('1970-01-01 09:00:00', '2000-03-11 09:00:00', '2000-03-11 09:00:00', '테스트 게시글 1', '1번 테스트 게시글입니다', 'test@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 2', '2번 테스트 게시글입니다', 'test@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 3', '3번 테스트 게시글입니다', 'test@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 4', '4번 테스트 게시글입니다', 'test@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 5', '5번 테스트 게시글입니다', 'test@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 6', '6번 테스트 게시글입니다', 'test@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 7', '7번 테스트 게시글입니다', 'test@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 8', '8번 테스트 게시글입니다', 'test@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 9', '9번 테스트 게시글입니다', 'test@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 10', '10번 테스트 게시글입니다', 'test@test.com'),
       ('1970-01-01 09:00:00', '2000-03-11 09:00:00', '2000-03-11 09:00:00', '테스트 게시글 11', '11번 테스트 게시글입니다', 'user@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 12', '12번 테스트 게시글입니다', 'user@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 13', '13번 테스트 게시글입니다', 'user@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 14', '14번 테스트 게시글입니다', 'user@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 15', '15번 테스트 게시글입니다', 'user@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 16', '16번 테스트 게시글입니다', 'user@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 17', '17번 테스트 게시글입니다', 'user@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 18', '18번 테스트 게시글입니다', 'user@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 19', '19번 테스트 게시글입니다', 'user@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 20', '20번 테스트 게시글입니다', 'user@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 21', '21번 테스트 게시글입니다', 'user@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 22', '22번 테스트 게시글입니다', 'user@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 23', '23번 테스트 게시글입니다', 'user@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 24', '24번 테스트 게시글입니다', 'user@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 25', '25번 테스트 게시글입니다', 'user@test.com'),
       ('1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '테스트 게시글 26', '26번 테스트 게시글입니다', 'user@test.com');


insert into notification (title, content, receiver_email, sender, deleted_at, created_at, updated_at)
values ('테스트 알림 1', '1번 테스트 알림입니다', 'user@test.com', 'system', '1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('테스트 알림 2', '2번 테스트 알림입니다', 'user@test.com', 'system', '1970-01-01 09:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


insert into comment(article_id, created_at, updated_at, deleted_at, author_email, content)
values (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com',
        '대통령은 내우·외환·천재·지변 또는 중대한 재정·경제상의 위기에 있어서 국가의 안전보장 또는 공공의 안녕질서를 유지하기 위하여 긴급한 조치가 필요하고 국회의 집회를 기다릴 여유가 없을 때에 한하여 최소한으로 필요한 재정·경제상의 처분을 하거나 이에 관하여 법률의 효력을 가지는 명령을 발할 수 있다.'),
       (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '헌법개정은 국회재적의원 과반수 또는 대통령의 발의로 제안된다.'),
       (1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '모든 국민은 자기의 행위가 아닌 친족의 행위로 인하여 불이익한 처우를 받지 아니한다.'),
       (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '대통령으로 선거될 수 있는 자는 국회의원의 피선거권이 있고 선거일 현재 40세에 달하여야 한다.'),
       (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '대법원에 대법관을 둔다. 다만, 법률이 정하는 바에 의하여 대법관이 아닌 법관을 둘 수 있다.'),
       (2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com',
        '모든 국민은 신체의 자유를 가진다. 누구든지 법률에 의하지 아니하고는 체포·구속·압수·수색 또는 심문을 받지 아니하며, 법률과 적법한 절차에 의하지 아니하고는 처벌·보안처분 또는 강제노역을 받지 아니한다.'),
       (3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '국회는 정부의 동의없이 정부가 제출한 지출예산 각항의 금액을 증가하거나 새 비목을 설치할 수 없다.'),
       (3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '국회의원은 국회에서 직무상 행한 발언과 표결에 관하여 국회외에서 책임을 지지 아니한다.'),
       (3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '누구든지 병역의무의 이행으로 인하여 불이익한 처우를 받지 아니한다.'),
       (4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com',
        '대통령은 국가의 원수이며, 외국에 대하여 국가를 대표한다. 행정각부의 장은 국무위원 중에서 국무총리의 제청으로 대통령이 임명한다.'),
       (4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '국가는 농·어민과 중소기업의 자조조직을 육성하여야 하며, 그 자율적 활동과 발전을 보장한다.'),
       (4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com',
        '대통령이 궐위된 때 또는 대통령 당선자가 사망하거나 판결 기타의 사유로 그 자격을 상실한 때에는 60일 이내에 후임자를 선거한다.'),
       (5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '대한민국의 주권은 국민에게 있고, 모든 권력은 국민으로부터 나온다.'),
       (5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '대통령은 국가의 독립·영토의 보전·국가의 계속성과 헌법을 수호할 책무를 진다.'),
       (5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com',
        '신체장애자 및 질병·노령 기타의 사유로 생활능력이 없는 국민은 법률이 정하는 바에 의하여 국가의 보호를 받는다.'),
       (6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com',
        '국가는 균형있는 국민경제의 성장 및 안정과 적정한 소득의 분배를 유지하고, 시장의 지배와 경제력의 남용을 방지하며, 경제주체간의 조화를 통한 경제의 민주화를 위하여 경제에 관한 규제와 조정을 할 수 있다.'),
       (6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com',
        '국회에 제출된 법률안 기타의 의안은 회기중에 의결되지 못한 이유로 폐기되지 아니한다. 다만, 국회의원의 임기가 만료된 때에는 그러하지 아니하다.'),
       (6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com',
        '모든 국민은 법 앞에 평등하다. 누구든지 성별·종교 또는 사회적 신분에 의하여 정치적·경제적·사회적·문화적 생활의 모든 영역에 있어서 차별을 받지 아니한다.'),
       (7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com',
        '모든 국민은 양심의 자유를 가진다. 국가는 과학기술의 혁신과 정보 및 인력의 개발을 통하여 국민경제의 발전에 노력하여야 한다.'),
       (7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '헌법재판소는 법관의 자격을 가진 9인의 재판관으로 구성하며, 재판관은 대통령이 임명한다.'),
       (7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com',
        '대통령은 내우·외환·천재·지변 또는 중대한 재정·경제상의 위기에 있어서 국가의 안전보장 또는 공공의 안녕질서를 유지하기 위하여 긴급한 조치가 필요하고 국회의 집회를 기다릴 여유가 없을 때에 한하여 최소한으로 필요한 재정·경제상의 처분을 하거나 이에 관하여 법률의 효력을 가지는 명령을 발할 수 있다.'),
       (8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '헌법개정은 국회재적의원 과반수 또는 대통령의 발의로 제안된다.'),
       (8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '모든 국민은 자기의 행위가 아닌 친족의 행위로 인하여 불이익한 처우를 받지 아니한다.'),
       (8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '대통령으로 선거될 수 있는 자는 국회의원의 피선거권이 있고 선거일 현재 41세에 달하여야 한다.'),
       (9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '대법원에 대법관을 둔다. 다만, 법률이 정하는 바에 의하여 대법관이 아닌 법관을 둘 수 있다.'),
       (9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com',
        '모든 국민은 신체의 자유를 가진다. 누구든지 법률에 의하지 아니하고는 체포·구속·압수·수색 또는 심문을 받지 아니하며, 법률과 적법한 절차에 의하지 아니하고는 처벌·보안처분 또는 강제노역을 받지 아니한다.'),
       (9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '국회는 정부의 동의없이 정부가 제출한 지출예산 각항의 금액을 증가하거나 새 비목을 설치할 수 없다.'),
       (10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '국회의원은 국회에서 직무상 행한 발언과 표결에 관하여 국회외에서 책임을 지지 아니한다.'),
       (10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '누구든지 병역의무의 이행으로 인하여 불이익한 처우를 받지 아니한다.'),
       (10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com',
        '대통령은 국가의 원수이며, 외국에 대하여 국가를 대표한다. 행정각부의 장은 국무위원 중에서 국무총리의 제청으로 대통령이 임명한다.'),
       (11, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '국가는 농·어민과 중소기업의 자조조직을 육성하여야 하며, 그 자율적 활동과 발전을 보장한다.'),
       (11, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com',
        '대통령이 궐위된 때 또는 대통령 당선자가 사망하거나 판결 기타의 사유로 그 자격을 상실한 때에는 61일 이내에 후임자를 선거한다.'),
       (11, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '대한민국의 주권은 국민에게 있고, 모든 권력은 국민으로부터 나온다.'),
       (12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '대통령은 국가의 독립·영토의 보전·국가의 계속성과 헌법을 수호할 책무를 진다.'),
       (12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com',
        '신체장애자 및 질병·노령 기타의 사유로 생활능력이 없는 국민은 법률이 정하는 바에 의하여 국가의 보호를 받는다.'),
       (12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com',
        '국가는 균형있는 국민경제의 성장 및 안정과 적정한 소득의 분배를 유지하고, 시장의 지배와 경제력의 남용을 방지하며, 경제주체간의 조화를 통한 경제의 민주화를 위하여 경제에 관한 규제와 조정을 할 수 있다.'),
       (13, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com',
        '국회에 제출된 법률안 기타의 의안은 회기중에 의결되지 못한 이유로 폐기되지 아니한다. 다만, 국회의원의 임기가 만료된 때에는 그러하지 아니하다.'),
       (13, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com',
        '모든 국민은 법 앞에 평등하다. 누구든지 성별·종교 또는 사회적 신분에 의하여 정치적·경제적·사회적·문화적 생활의 모든 영역에 있어서 차별을 받지 아니한다.'),
       (13, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com',
        '모든 국민은 양심의 자유를 가진다. 국가는 과학기술의 혁신과 정보 및 인력의 개발을 통하여 국민경제의 발전에 노력하여야 한다.'),
       (14, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '헌법재판소는 법관의 자격을 가진 10인의 재판관으로 구성하며, 재판관은 대통령이 임명한다.'),
       (14, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com',
        '대통령은 내우·외환·천재·지변 또는 중대한 재정·경제상의 위기에 있어서 국가의 안전보장 또는 공공의 안녕질서를 유지하기 위하여 긴급한 조치가 필요하고 국회의 집회를 기다릴 여유가 없을 때에 한하여 최소한으로 필요한 재정·경제상의 처분을 하거나 이에 관하여 법률의 효력을 가지는 명령을 발할 수 있다.'),
       (14, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '헌법개정은 국회재적의원 과반수 또는 대통령의 발의로 제안된다.'),
       (15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '모든 국민은 자기의 행위가 아닌 친족의 행위로 인하여 불이익한 처우를 받지 아니한다.'),
       (15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '대통령으로 선거될 수 있는 자는 국회의원의 피선거권이 있고 선거일 현재 42세에 달하여야 한다.'),
       (15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '대법원에 대법관을 둔다. 다만, 법률이 정하는 바에 의하여 대법관이 아닌 법관을 둘 수 있다.'),
       (16, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com',
        '대통령은 내우·외환·천재·지변 또는 중대한 재정·경제상의 위기에 있어서 국가의 안전보장 또는 공공의 안녕질서를 유지하기 위하여 긴급한 조치가 필요하고 국회의 집회를 기다릴 여유가 없을 때에 한하여 최소한으로 필요한 재정·경제상의 처분을 하거나 이에 관하여 법률의 효력을 가지는 명령을 발할 수 있다.'),
       (16, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '헌법개정은 국회재적의원 과반수 또는 대통령의 발의로 제안된다.'),
       (16, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '모든 국민은 자기의 행위가 아닌 친족의 행위로 인하여 불이익한 처우를 받지 아니한다.'),
       (17, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '대통령으로 선거될 수 있는 자는 국회의원의 피선거권이 있고 선거일 현재 41세에 달하여야 한다.'),
       (17, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '대법원에 대법관을 둔다. 다만, 법률이 정하는 바에 의하여 대법관이 아닌 법관을 둘 수 있다.'),
       (17, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com',
        '모든 국민은 신체의 자유를 가진다. 누구든지 법률에 의하지 아니하고는 체포·구속·압수·수색 또는 심문을 받지 아니하며, 법률과 적법한 절차에 의하지 아니하고는 처벌·보안처분 또는 강제노역을 받지 아니한다.'),
       (18, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '국회는 정부의 동의없이 정부가 제출한 지출예산 각항의 금액을 증가하거나 새 비목을 설치할 수 없다.'),
       (18, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '국회의원은 국회에서 직무상 행한 발언과 표결에 관하여 국회외에서 책임을 지지 아니한다.'),
       (18, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '누구든지 병역의무의 이행으로 인하여 불이익한 처우를 받지 아니한다.'),
       (19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com',
        '대통령은 국가의 원수이며, 외국에 대하여 국가를 대표한다. 행정각부의 장은 국무위원 중에서 국무총리의 제청으로 대통령이 임명한다.'),
       (19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '국가는 농·어민과 중소기업의 자조조직을 육성하여야 하며, 그 자율적 활동과 발전을 보장한다.'),
       (19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com',
        '대통령이 궐위된 때 또는 대통령 당선자가 사망하거나 판결 기타의 사유로 그 자격을 상실한 때에는 61일 이내에 후임자를 선거한다.'),
       (20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com', '대한민국의 주권은 국민에게 있고, 모든 권력은 국민으로부터 나온다.'),
       (20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'test@test.com', '대통령은 국가의 독립·영토의 보전·국가의 계속성과 헌법을 수호할 책무를 진다.'),
       (20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '1970-01-01 09:00:00', 'user@test.com',
        '신체장애자 및 질병·노령 기타의 사유로 생활능력이 없는 국민은 법률이 정하는 바에 의하여 국가의 보호를 받는다.');
