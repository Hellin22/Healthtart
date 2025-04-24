DROP TABLE IF EXISTS rival;
DROP TABLE IF EXISTS inbody;
DROP TABLE IF EXISTS record_per_user;
DROP TABLE IF EXISTS recommended_workout_history;
DROP TABLE IF EXISTS workout_per_routine;
DROP TABLE IF EXISTS equipment_per_gym;
DROP TABLE IF EXISTS exercise_equipment;
DROP TABLE IF EXISTS workout_info;
DROP TABLE IF EXISTS routines;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS gym;


CREATE TABLE gym (
                     gym_code BIGINT  NOT NULL AUTO_INCREMENT,
                     gym_name VARCHAR(255),
                     address VARCHAR(255),
                     business_number VARCHAR(255),
                     created_at DATETIME DEFAULT NOW(),
                     updated_at DATETIME DEFAULT NOW(),
                     PRIMARY KEY (gym_code)
);

CREATE TABLE users (
                       user_code VARCHAR(255) NOT NULL,
                       user_type VARCHAR(255) NOT NULL,
                       user_name VARCHAR(255),
                       user_email VARCHAR(255),
                       user_password VARCHAR(255),
                       user_phone VARCHAR(255),
                       user_nickname VARCHAR(255),
                       user_address VARCHAR(255),
                       user_flag BOOLEAN DEFAULT TRUE NOT NULL,
                       user_gender VARCHAR(1) CHECK (user_gender IN ('M', 'F')),
                       user_height DOUBLE ,
                       user_weight DOUBLE ,
                       user_age INTEGER ,
                       provider VARCHAR(255),
                       provider_id VARCHAR(255),
                       created_at DATETIME NOT NULL,
                       updated_at DATETIME NOT NULL,
                       gym_code BIGINT,
                       FOREIGN KEY (gym_code) REFERENCES gym(gym_code) ON DELETE SET NULL,
                       PRIMARY KEY (user_code)
);

CREATE TABLE routines (
                          routine_code BIGINT NOT NULL AUTO_INCREMENT,
                          created_at DATETIME DEFAULT NOW(),
                          updated_at DATETIME DEFAULT NOW(),
                          PRIMARY KEY(routine_code)
);

CREATE TABLE workout_info (
                              workout_info_code BIGINT NOT NULL AUTO_INCREMENT,
                              title VARCHAR(255) NOT NULL,
                              workout_time INT NOT NULL ,
                              recommend_music VARCHAR(255),
                              created_at DATETIME DEFAULT NOW(),
                              updated_at DATETIME DEFAULT NOW(),
                              routine_code BIGINT,
                              FOREIGN KEY (routine_code) REFERENCES routines(routine_code) ON DELETE SET NULL,
                              PRIMARY KEY(workout_info_code)
);

CREATE TABLE exercise_equipment (
                                    exercise_equipment_code BIGINT NOT NULL AUTO_INCREMENT,
                                    exercise_equipment_name VARCHAR(255),
                                    body_part VARCHAR(255),
                                    exercise_description VARCHAR(255) DEFAULT NULL,
                                    exercise_image VARCHAR(255) DEFAULT NULL,
                                    recommended_video VARCHAR(255) DEFAULT NULL,
                                    created_at DATETIME DEFAULT NOW(),
                                    updated_at DATETIME DEFAULT NOW(),
                                    PRIMARY KEY (exercise_equipment_code)
);

CREATE TABLE equipment_per_gym (
                                   equipment_per_gym_code BIGINT NOT NULL AUTO_INCREMENT,
                                   created_at DATETIME DEFAULT NOW(),
                                   updated_at DATETIME DEFAULT NOW(),
                                   gym_code BIGINT  NOT NULL,
                                   exercise_equipment_code BIGINT  NOT NULL,
                                   FOREIGN KEY (gym_code) REFERENCES gym(gym_code) ON DELETE CASCADE,
                                   FOREIGN KEY (exercise_equipment_code) REFERENCES exercise_equipment(exercise_equipment_code) ON DELETE CASCADE,
                                   PRIMARY KEY (equipment_per_gym_code)
);

CREATE TABLE workout_per_routine (
                                     workout_per_routine_code BIGINT NOT NULL AUTO_INCREMENT,
                                     workout_order INTEGER,
                                     workout_name VARCHAR(255) NOT NULL ,
                                     link VARCHAR(255),
                                     weight_set INTEGER,
                                     number_per_set INTEGER,
                                     weight_per_set INTEGER,
                                     workout_time INTEGER,
                                     created_at DATETIME DEFAULT NOW(),
                                     updated_at DATETIME DEFAULT NOW(),
                                     routine_code BIGINT  NOT NULL,
                                     exercise_equipment_code BIGINT  NOT NULL,
                                     FOREIGN KEY (exercise_equipment_code) REFERENCES exercise_equipment(exercise_equipment_code) ON DELETE CASCADE,
                                     FOREIGN KEY (routine_code) REFERENCES routines(routine_code) ON DELETE CASCADE,
                                     PRIMARY KEY (workout_per_routine_code)
);

CREATE TABLE recommended_workout_history (
                                             history_code BIGINT  NOT NULL AUTO_INCREMENT,
                                             routine_ratings DOUBLE,
                                             created_at DATETIME DEFAULT NOW(),
                                             updated_at DATETIME DEFAULT NOW(),
                                             workout_info_code BIGINT  NOT NULL,
                                             FOREIGN KEY (workout_info_code) REFERENCES workout_info(workout_info_code) ON DELETE CASCADE,
                                             PRIMARY KEY (history_code)
);

CREATE TABLE record_per_user (
                                 user_record_code BIGINT  NOT NULL AUTO_INCREMENT,
                                 day_of_exercise DATETIME,
                                 exercise_duration INTEGER,
                                 record_flag BOOLEAN DEFAULT TRUE,
                                 created_at DATETIME DEFAULT NOW(),
                                 updated_at DATETIME DEFAULT NOW(),
                                 user_code VARCHAR(255) NOT NULL,
                                 routine_code BIGINT  NOT NULL,
                                 PRIMARY KEY (user_record_code),
                                 FOREIGN KEY (routine_code) REFERENCES routines(routine_code) ON DELETE CASCADE,
                                 FOREIGN KEY (user_code) REFERENCES users(user_code) ON DELETE CASCADE
);

CREATE TABLE inbody (
                        inbody_code BIGINT  NOT NULL AUTO_INCREMENT,
                        inbody_score INTEGER,
                        weight DOUBLE,
                        height DOUBLE,
                        muscle_weight DOUBLE,
                        fat_weight DOUBLE,
                        bmi DOUBLE,
                        fat_percentage DOUBLE,
                        day_of_inbody DATETIME,
                        basal_metabolic_rate INTEGER,
                        created_at DATETIME DEFAULT NOW(),
                        updated_at DATETIME DEFAULT NOW(),
                        user_code VARCHAR(255) NOT NULL,
                        PRIMARY KEY (inbody_code),
                        FOREIGN KEY (user_code) REFERENCES users(user_code) ON DELETE CASCADE
);

CREATE TABLE rival (
                       rival_match_code BIGINT  NOT NULL AUTO_INCREMENT,
                       user_code VARCHAR(255) NOT NULL,
                       rival_user_code VARCHAR(255) NOT NULL,
                       created_at DATETIME DEFAULT NOW(),
                       updated_at DATETIME DEFAULT NOW(),
                       PRIMARY KEY (rival_match_code),
                       FOREIGN KEY (user_code) REFERENCES users(user_code) ON DELETE CASCADE,
                       FOREIGN KEY (rival_user_code) REFERENCES users(user_code) ON DELETE CASCADE
);

INSERT INTO gym (gym_code, gym_name, address, business_number)
 VALUES
	   (1, '어게인짐 신대방삼거리역', '서울 동작구 상도로 83 2층 어게인짐', '110-81-34859'),
	   (2, '지앤지 헬스케어', '서울 동작구 신대방동 385-4', '110-81-25891'),
	   (3, '피트니스코어', '서울 동작구 신대방동 1-8', '110-81-34860'),
	   (4, '엑스핏', '서울 동작구 상도로 78', '110-81-25892'),
	   (5, '골프존헬스', '서울 동작구 신대방동 414-5', '110-81-34861'),
	   (6, '파워짐', '서울 동작구 신대방동 420', '110-81-25893'),
	   (7, '헬스타운', '서울 동작구 신대방동 382-10', '110-81-34862'),
	   (8, '스포애니 신대방점', '서울 동작구 신대방동 397', '110-81-25894'),
	   (9, '신대방 스포츠센터', '서울 동작구 신대방동 366-9', '110-81-34863'),
	   (10, 'D.O.M 헬스장', '서울 동작구 신대방동 370-8', '110-81-25895');

INSERT INTO users (user_code, user_type, user_name, user_email, user_password, user_phone, user_nickname, user_address, user_flag, user_gender, user_height, user_weight, user_age, provider, provider_id, created_at, updated_at, gym_code)
VALUES
('20241007-36225461-e4f3-46de-8f97-119f2785deed', 'ADMIN', '홍길동', 'test1@test.com', '$2b$12$IW.t/QZOTdI58NQhlej4.O7zek/gfhJqhOYdI2dy0adR24rvvkJPC', '010-1234-5670', 'nickname1', 'Address 1', TRUE, 'M', 175, 70, 30, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-c3642896-d092-474d-9278-a9972f9dee22', 'MEMBER', '강동원', 'test2@test.com', '$2b$12$l662C6nYclLz/gAdHlYF9O7.TyaLwaXLZTVNXqYVf3utXRtvbxn2O', '010-1234-5671', 'nickname2', 'Address 2', TRUE, 'M', 180, 75, 32, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-fc0f20f4-8cf8-4a16-8a4e-369a3f05b993', 'MEMBER', '이민호', 'test3@test.com', '$2b$12$/BWvpiYLumeFEOc68506DeWjx1vP77HhGuwOIO.s/htiga8v9G50O', '010-1234-5672', 'nickname3', 'Address 3', TRUE, 'M', 182, 78, 33, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-c1bf0883-d973-48cf-a8ad-47a7dc9d7d8b', 'MEMBER', '박보검', 'test4@test.com', '$2b$12$cq4oZMZ/hO.LJ4E3E8gc.OCwDe334AVtx2qB01eaFRHxdiVgoibku', '010-1234-5673', 'nickname4', 'Address 4', TRUE, 'M', 176, 72, 29, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-05bfb06b-8eda-4857-8681-40d1eccb829d', 'MEMBER', '전지현', 'test5@test.com', '$2b$12$/laGN.c./7ZvAuOgFsIRRO8bGe/SuyGuyq1nzThfk8RB3mxPwdR0S', '010-1234-5674', 'nickname5', 'Address 5', TRUE, 'F', 168, 55, 28, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-0762dbf6-8176-41c2-a656-3c4092656ca2', 'MEMBER', '한지민', 'test6@test.com', '$2b$12$PjvVj4/10DkDHPu52lqu3eWgAMv1JPCIk/M2mHvVLc4Td3JKD.4pi', '010-1234-5675', 'nickname6', 'Address 6', TRUE, 'F', 165, 53, 26, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-1f1960d2-212d-4d01-8b0a-4485b9d69d5a', 'MEMBER', '김수현', 'test7@test.com', '$2b$12$Ahiqh/4paTuc9I5k/DUCMO2dqFI8u.oSGfplZmMSv2gYpuSz1jVNu', '010-1234-5676', 'nickname7', 'Address 7', TRUE, 'M', 178, 68, 31, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-6ebfa239-01e9-4597-97b8-a28be50ed664', 'MEMBER', '송중기', 'test8@test.com', '$2b$12$ajQGDJgwhK0GQKP4GLM/h.vXF0wWfE5JxH15U2zBH1EhPx7kbmNpS', '010-1234-5677', 'nickname8', 'Address 8', TRUE, 'M', 180, 74, 34, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-7a3cc3ef-3901-4aaa-846b-5e99f355257f', 'MEMBER', '김태리', 'test9@test.com', '$2b$12$6JIfKjS5Swq1.T4cfzVZae4I5FdqgyBY1tSjb3K0VEBRBjSsPIEEK', '010-1234-5678', 'nickname9', 'Address 9', TRUE, 'F', 164, 52, 27, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-f4252be3-45cc-4318-98dd-ed56593cfc53', 'MEMBER', '정해인', 'test10@test.com', '$2b$12$TO6KzPc1yJeMVlNmt12meOy6LDWo0jppbc1VyHTtfLeJEqZbd/dae', '010-1234-5679', 'nickname10', 'Address 10', TRUE, 'M', 177, 73, 32, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-79f61b0b-3282-48b7-8a3c-9dd83d2cfec2', 'MEMBER', '장호정', 'test31@test.com', '$2b$12$Wb9RyWAM3Klec5Er3UsWx.v68YKnC0O2QnG.v3LNVjzDNdv/azP9a', '010-1234-5701', '갓호정', 'Address 31', TRUE, 'F', 170, 55, 25, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-9e7cc4e0-022e-46eb-ad78-02acb7aab693', 'MEMBER', '양현진', 'test32@test.com', '$2b$12$SYlkIIl1EqrE6VEA1kVkQeWmhWu/yFCBzAC/sgWwvJqz3b5CpIK92', '010-1234-5702', '갓현진', 'Address 32', TRUE, 'F', 170, 55, 33, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-d58e2719-4c3d-4b75-8621-ce2313a358bf', 'MEMBER', '박경희', 'test33@test.com', '$2b$12$faG0Dj9TuFzfvosv7.hq.e1ndVuZc9o1mPalMxxLhTbPR.b7ypeGS', '010-1234-5703', '갓경희', 'Address 33', TRUE, 'F', 170, 55, 30, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-7430a349-93ab-4b1d-a3ac-301ca97589e9', 'MEMBER', '노다민', 'test34@test.com', '$2b$12$/qoey5wXujZ.0wRteRVFfualyGfJj422yNJzGYEk.Tb5SDu4T.gFW', '010-1234-5704', '갓다민', 'Address 34', TRUE, 'F', 170, 55, 31, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-8b202235-a8a7-4772-beb0-25b9e66ea10e', 'MEMBER', '윤채연', 'test35@test.com', '$2b$12$nfycSFvMGR0lwn.Z6KuNWuA4SX7ZusYGRZQqbNxxqZezDdZj0TxrC', '010-1234-5705', '갓채연', 'Address 35', TRUE, 'F', 170, 55, 29, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-d8341165-c9db-4234-9376-16618a332f20', 'MEMBER', '김시우', 'test36@test.com', '$2b$12$Vo1WaP1XFInewb4VrEMZreTT7owxKVw9RQobn1D4NxWvgNViHn.Ri', '010-1234-5706', 'KIMSIUUU', 'Address 36', TRUE, 'M', 187, 80, 25, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-df0f55fd-0f9a-498f-9f53-651cb2e60b14', 'MEMBER', '기우석', 'test37@test.com', '$2b$12$2fpKvAWuNTH8foX8EiR3QOlNH91ly7q3NlscOfKxi.v.9ddMjJrJq', '010-1234-5707', 'KIUUU', 'Address 37', TRUE, 'M', 187, 80, 28, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-6ac00e92-24b1-487d-89e6-54f86e71c6e4', 'MEMBER', '이효진', 'test38@test.com', '$2b$12$pkVPUuR1ODmZQ0MYPXhK/.vOgvJf29wrrPhdTMyLt9HkY/YqZ0e8S', '010-1234-5708', '갓효진', 'Address 38', TRUE, 'F', 170, 55, 27, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-8bcabbfc-0f2f-4812-b819-e0cf79a9d041', 'MEMBER', '송의혁', 'test39@test.com', '$2b$12$pNsUvV.POVkUvw.QNRr9hOOeMwMtsID8tFi9y5IRGVZJP6EdcN/yG', '010-1234-5709', '대의혁', 'Address 39', TRUE, 'M', 187, 80, 30, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-41f24a02-0c39-47c1-8f9b-b83219d5936f', 'MEMBER', '용길한', 'test40@test.com', '$2b$12$BFlsObGxeNGZHC2QeNFxGeFXJMGNzy32ewWvN.O/F9zzXgf86knX6', '010-1234-5710', '갓길한', 'Address 40', TRUE, 'M', 187, 80, 29, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-f7742c80-3299-442f-9405-9cf42b92d16f', 'MEMBER', '정준서', 'test41@test.com', '$2b$12$1okg6CsAmzx7HJTLw1I11O9I5eOnM/voLKwW4CbNfEBwKAKNxMAWe', '010-1234-5711', '대준서', 'Address 41', TRUE, 'M', 187, 80, 32, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-00d92a1c-3a7c-49b7-9c34-7b6cfab7b40c', 'MEMBER', '방동호', 'test42@test.com', '$2b$12$SB5LJyzV9wwHGP5P5kGfYuivcvhLyzyVJDJu9QNZsphtJdgy/8Zta', '010-1234-5712', '갓동호', 'Address 42', TRUE, 'M', 187, 80, 33, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-fdefd1f4-3d09-4d5e-bfa2-0f991bdfad35', 'MEMBER', '유혜진', 'test43@test.com', '$2b$12$dC3JlG6aM86DbWBuH0RdGe8MOmAtJnhksrIUlNE3HFIO5Z3uRrrVS', '010-1234-5713', '갓혜진', 'Address 43', TRUE, 'F', 170, 55, 30, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-49b6402d-cd09-45ec-bf0e-cd6bb5e43dd7', 'MEMBER', '이우진', 'test44@test.com', '$2b$12$B2uVCROEJvpfrrj3z46Wv.bXT3nqUGCqQitNZSXBpccyV3QPVdXKu', '010-1234-5714', '대우진', 'Address 44', TRUE, 'M', 187, 80, 28, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-45acb0ae-b91a-4d22-a3db-248872314656', 'MEMBER', '전기범', 'test45@test.com', '$2b$12$3TXpxM9fCLRmpe75xxG7SO8mxC3kJrhAl0Dq7veWmeuYqWvQsy1xC', '010-1234-5715', '전기범입니다', 'Address 45', TRUE, 'M', 187, 80, 30, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-68e7b181-3cb7-49e1-97ef-c2ba3d8373f8', 'MEMBER', '김민석', 'test46@test.com', '$2b$12$RKFG9heHR0J6Ls.vUtpBuOZmUlGn3EwQOXZLf25.PUO0ejKwTmVkm', '010-1234-5716', '김민석나가네', 'Address 46', TRUE, 'M', 187, 80, 33, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-c79935e9-449f-4853-81f5-07bb1769f4b3', 'MEMBER', '조창욱', 'test47@test.com', '$2b$12$d7xiWdHl5R/a/USi4eGl0.7ym/3LSD5nlJ5Nm0jEGgQ9vohvPjpmO', '010-1234-5717', '갓창욱', 'Address 47', TRUE, 'M', 187, 80, 30, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-c66f0e04-6f7b-4f7d-bb97-dfe3581c7431', 'MEMBER', '김서현', 'test48@test.com', '$2b$12$8ZTSJ.aZmN0PExHYXpyEIOylVnZJDvVqSZFBVzBoO.LzdRPrkXmxS', '010-1234-5718', '대서현', 'Address 48', TRUE, 'F', 170, 55, 28, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-70cf5cf9-dc88-49c2-864f-57fdc391da26', 'MEMBER', '최해관', 'test49@test.com', '$2b$12$pu.gqMtyhswkr.p57KISFeAfKDsIInMvAlOWFD2uD5IE25aCJwRdi', '010-1234-5719', '갓해관', 'Address 49', TRUE, 'M', 187, 80, 31, NULL, NULL, NOW(), NOW(), NULL),
    ('20241007-1403fb06-4955-40d6-9911-9876ba2db233', 'MEMBER', '김정모', 'test50@test.com', '$2b$12$l662C6nYclLz/gAdHlYF9O7.TyaLwaXLZTVNXqYVf3utXRtvbxn2O', '010-1234-5720', '모짱', 'Address 50', TRUE, 'M', 187, 80, 27, NULL, NULL, NOW(), NOW(), NULL),
    ('20241021-1a2b3c4d-5e6f-7g8h-9i0j-k1l2m3n4o5p6', 'MEMBER', '조제훈', 'test51@test.com', '$2b$12$l662C6nYclLz/gAdHlYF9O7.TyaLwaXLZTVNXqYVf3utXRtvbxn2O', '010-1234-5721', '5조찍5조', 'Address 51', TRUE, 'M', 187, 80, 28, NULL, NULL, NOW(), NOW(), NULL),
    ('20241021-6f7g8h9i-0j1k-2l3m-4n5o-6p7q8r9s0t1u', 'MEMBER', '백경석', 'test52@test.com', '$2b$12$l662C6nYclLz/gAdHlYF9O7.TyaLwaXLZTVNXqYVf3utXRtvbxn2O', '010-1234-5722', '대경석', 'Address 52', TRUE, 'M', 187, 80, 32, NULL, NULL, NOW(), NOW(), NULL),
    ('20241021-9i0j1k2l-3m4n-5o6p-7q8r-9s0t1u2v3w4x', 'MEMBER', '유제은', 'test53@test.com', '$2b$12$l662C6nYclLz/gAdHlYF9O7.TyaLwaXLZTVNXqYVf3utXRtvbxn2O', '010-1234-5723', '대제은', 'Address 53', TRUE, 'F', 170, 55, 29, NULL, NULL, NOW(), NOW(), NULL),
    ('20241021-4n5o6p7q-8r9s-0t1u-2v3w-4x5y6z7a8b9c', 'MEMBER', '이나현', 'test54@test.com', '$2b$12$l662C6nYclLz/gAdHlYF9O7.TyaLwaXLZTVNXqYVf3utXRtvbxn2O', '010-1234-5724', '이나짱', 'Address 54', TRUE, 'F', 170, 55, 27, NULL, NULL, NOW(), NOW(), NULL),
    ('20241021-7q8r9s0t-1u2v-3w4x-5y6z-7a8b9c0d1e2f', 'MEMBER', '이서현', 'test55@test.com', '$2b$12$l662C6nYclLz/gAdHlYF9O7.TyaLwaXLZTVNXqYVf3utXRtvbxn2O', '010-1234-5725', '갓서현', 'Address 55', TRUE, 'F', 170, 55, 25, NULL, NULL, NOW(), NOW(), NULL),
    ('20241021-2v3w4x5y-6z7a-8b9c-0d1e-2f3g4h5i6j7k', 'MEMBER', '장민근', 'test56@test.com', '$2b$12$l662C6nYclLz/gAdHlYF9O7.TyaLwaXLZTVNXqYVf3utXRtvbxn2O', '010-1234-5726', '대민근', 'Address 56', TRUE, 'M', 187, 80, 30, NULL, NULL, NOW(), NOW(), NULL),
    ('20241021-5y6z7a8b-9c0d-1e2f-3g4h-5i6j7k8l9m0n', 'MEMBER', '김동혁', 'test57@test.com', '$2b$12$l662C6nYclLz/gAdHlYF9O7.TyaLwaXLZTVNXqYVf3utXRtvbxn2O', '010-1234-5727', '갓동혁', 'Address 57', TRUE, 'M', 187, 80, 28, NULL, NULL, NOW(), NOW(), NULL);



INSERT INTO inbody (inbody_score, weight, height, muscle_weight, fat_weight, bmi, fat_percentage, day_of_inbody, basal_metabolic_rate, created_at, updated_at, user_code)
VALUES
   (80, 75, 180, 25.0, 12.5, 23.1, 17.0, '2024-09-25 08:00:00', 1500, '2024-09-25 08:10:00', '2024-09-25 08:10:00', '20241007-c3642896-d092-474d-9278-a9972f9dee22'),
   (82, 78, 182, 26.0, 13.0, 24.0, 18.0, '2024-09-26 09:00:00', 1550, '2024-09-26 09:15:00', '2024-09-26 09:15:00', '20241007-fc0f20f4-8cf8-4a16-8a4e-369a3f05b993'),
   (81, 72, 176, 24.5, 11.5, 22.0, 16.5, '2024-09-27 10:00:00', 1480, '2024-09-27 10:10:00', '2024-09-27 10:10:00', '20241007-c1bf0883-d973-48cf-a8ad-47a7dc9d7d8b'),
   (83, 55, 168, 22.0, 9.0, 21.5, 19.0, '2024-09-28 11:00:00', 1300, '2024-09-28 11:20:00', '2024-09-28 11:20:00', '20241007-05bfb06b-8eda-4857-8681-40d1eccb829d'),
   (84, 53, 165, 21.5, 8.5, 20.5, 18.5, '2024-09-29 12:00:00', 1250, '2024-09-29 12:15:00', '2024-09-29 12:15:00', '20241007-0762dbf6-8176-41c2-a656-3c4092656ca2'),
   (86, 68, 178, 24.0, 11.0, 22.5, 17.5, '2024-09-30 13:00:00', 1600, '2024-09-30 13:20:00', '2024-09-30 13:20:00', '20241007-1f1960d2-212d-4d01-8b0a-4485b9d69d5a'),
   (87, 74, 180, 25.5, 12.0, 23.5, 18.0, '2024-10-01 07:00:00', 1650, '2024-10-01 07:10:00', '2024-10-01 07:10:00', '20241007-6ebfa239-01e9-4597-97b8-a28be50ed664'),
    -- 밑에 4개는 정해인, 김태리꺼고 이 둘은 2개의 인바디 데이터를 등록했음.
   (85, 52, 164, 23.5, 10.2, 19.3, 18.7, '2024-10-01 08:00:00', 1450, '2024-10-01 08:10:00', '2024-10-01 08:10:00', '20241007-7a3cc3ef-3901-4aaa-846b-5e99f355257f'),
   (88, 52.5, 164, 24.0, 10.0, 19.4, 18.5, '2024-10-02 10:00:00', 1460, '2024-10-02 10:20:00', '2024-10-02 10:20:00', '20241007-7a3cc3ef-3901-4aaa-846b-5e99f355257f'),
   (90, 73, 177, 26.3, 12.4, 22.5, 17.0, '2024-10-01 09:30:00', 1700, '2024-10-01 09:45:00', '2024-10-01 09:45:00', '20241007-f4252be3-45cc-4318-98dd-ed56593cfc53'),
   (92, 73.5, 177, 26.8, 12.0, 22.7, 16.5, '2024-10-03 11:30:00', 1710, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-f4252be3-45cc-4318-98dd-ed56593cfc53'),

   (35, 55, 170, 24.7, 10.3, 19.0, 18.7, '2024-10-03 11:30:00', 1283, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-79f61b0b-3282-48b7-8a3c-9dd83d2cfec2'),
	(64, 55, 170, 21.6, 13.3, 19.0, 24.2, '2024-10-03 11:30:00', 1225, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-9e7cc4e0-022e-46eb-ad78-02acb7aab693'),
	(15, 55, 170, 21.8, 11.7, 19.0, 21.3, '2024-10-03 11:30:00', 1719, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-d58e2719-4c3d-4b75-8621-ce2313a358bf'),
	(72, 55, 170, 23.1, 8.5, 19.0, 15.5, '2024-10-03 11:30:00', 1578, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-7430a349-93ab-4b1d-a3ac-301ca97589e9'),
	(19, 55, 170, 20.8, 8.9, 19.0, 16.2, '2024-10-03 11:30:00', 1290, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-8b202235-a8a7-4772-beb0-25b9e66ea10e'),
	(80, 80, 187, 33.3, 13.9, 22.9, 17.4, '2024-10-03 11:30:00', 1209, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-d8341165-c9db-4234-9376-16618a332f20'),
	(98, 80, 187, 30.9, 17.0, 22.9, 21.2, '2024-10-03 11:30:00', 1375, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-df0f55fd-0f9a-498f-9f53-651cb2e60b14'),
	(26, 55, 170, 20.2, 13.0, 19.0, 23.6, '2024-10-03 11:30:00', 1756, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-6ac00e92-24b1-487d-89e6-54f86e71c6e4'),
	(66, 80, 187, 30.4, 15.5, 22.9, 19.4, '2024-10-03 11:30:00', 1968, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-8bcabbfc-0f2f-4812-b819-e0cf79a9d041'),
	(60, 80, 187, 29.4, 16.2, 22.9, 20.2, '2024-10-03 11:30:00', 1481, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-41f24a02-0c39-47c1-8f9b-b83219d5936f'),
	(62, 80, 187, 34.0, 16.8, 22.9, 21.0, '2024-10-03 11:30:00', 1775, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-f7742c80-3299-442f-9405-9cf42b92d16f'),
	(39, 80, 187, 32.8, 12.2, 22.9, 15.2, '2024-10-03 11:30:00', 1912, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-00d92a1c-3a7c-49b7-9c34-7b6cfab7b40c'),
	(31, 55, 170, 24.5, 10.5, 19.0, 19.1, '2024-10-03 11:30:00', 1422, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-fdefd1f4-3d09-4d5e-bfa2-0f991bdfad35'),
	(56, 80, 187, 30.5, 16.8, 22.9, 21.0, '2024-10-03 11:30:00', 1840, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-49b6402d-cd09-45ec-bf0e-cd6bb5e43dd7'),
	(13, 80, 187, 31.1, 19.9, 22.9, 24.9, '2024-10-03 11:30:00', 1977, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-45acb0ae-b91a-4d22-a3db-248872314656'),
	(65, 80, 187, 28.6, 14.0, 22.9, 17.5, '2024-10-03 11:30:00', 1748, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-68e7b181-3cb7-49e1-97ef-c2ba3d8373f8'),
	(98, 80, 187, 31.2, 15.7, 22.9, 19.6, '2024-10-03 11:30:00', 1772, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-c79935e9-449f-4853-81f5-07bb1769f4b3'),
	(10, 55, 170, 20.0, 13.3, 19.0, 24.2, '2024-10-03 11:30:00', 1525, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-c66f0e04-6f7b-4f7d-bb97-dfe3581c7431'),
	(74, 80, 187, 31.2, 19.9, 22.9, 24.9, '2024-10-03 11:30:00', 1299, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-70cf5cf9-dc88-49c2-864f-57fdc391da26'),
	(12, 80, 187, 29.7, 17.8, 22.9, 22.2, '2024-10-03 11:30:00', 1687, '2024-10-03 11:50:00', '2024-10-03 11:50:00', '20241007-1403fb06-4955-40d6-9911-9876ba2db233');


    INSERT INTO rival (user_code, rival_user_code, created_at, updated_at)
VALUES
    ('20241007-fc0f20f4-8cf8-4a16-8a4e-369a3f05b993', '20241007-c3642896-d092-474d-9278-a9972f9dee22', NOW(), NOW()),
    ('20241007-7a3cc3ef-3901-4aaa-846b-5e99f355257f', '20241007-f4252be3-45cc-4318-98dd-ed56593cfc53', NOW(), NOW()),
    ('20241007-f4252be3-45cc-4318-98dd-ed56593cfc53', '20241007-7a3cc3ef-3901-4aaa-846b-5e99f355257f', NOW(), NOW()),
    ('20241007-c3642896-d092-474d-9278-a9972f9dee22', '20241007-f4252be3-45cc-4318-98dd-ed56593cfc53', NOW(), NOW());