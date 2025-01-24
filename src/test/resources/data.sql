INSERT INTO user(email, nickname, profile_image, role, username)
values ('test1@test.com', 'test1', null, 'ROLE_USER', '테스트1');
INSERT INTO user(email, nickname, profile_image, role, username)
values ('test2@test.com', 'test2', null, 'ROLE_USER', '테스트2');
INSERT INTO user(email, nickname, profile_image, role, username)
values ('test3@test.com', 'test3', null, 'ROLE_USER', '테스트3');
INSERT INTO user(email, nickname, profile_image, role, username)
values ('test4@test.com', 'test4', null, 'ROLE_USER', '테스트4');
INSERT INTO user(email, nickname, profile_image, role, username)
values ('test5@test.com', 'test5', null, 'ROLE_USER', '테스트5');
INSERT INTO user(email, nickname, profile_image, role, username)
values ('test6@test.com', 'test6', null, 'ROLE_USER', '테스트6');
INSERT INTO user(email, nickname, profile_image, role, username)
values ('test7@test.com', 'test7', null, 'ROLE_USER', '테스트7');
INSERT INTO user(email, nickname, profile_image, role, username)
values ('test8@test.com', 'test8', null, 'ROLE_USER', '테스트8');
INSERT INTO user(email, nickname, profile_image, role, username)
values ('test9@test.com', 'test9', null, 'ROLE_USER', '테스트9');
INSERT INTO user(email, nickname, profile_image, role, username)
values ('test10@test.com', 'test10', null, 'ROLE_USER', '테스트10');

INSERT INTO group_table(group_name, photo_path, group_reg_date) VALUES ('테스트그룹', null, NOW());

INSERT INTO group_user(last_updated_date, reg_date, status, group_id, user_id)