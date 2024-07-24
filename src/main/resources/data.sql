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
