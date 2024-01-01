insert into USERS ( id, username,email,password) values (1,'user','John_Normal@email.com','$2a$10$xdbKoM48VySZqVSU/cSlVeJn0Z04XCZ7KZBjUBC00eKo5uLswyOpe');

insert into user_roles (user_id, roles) values ('1', 'USER');

insert into USERS (id, username, email, password) values (2, 'admin', 'Emma_Powerful@email.com', '$2a$10$jpLNVNeA7Ar/ZQ2DKbKCm.MuT2ESe.Qop96jipKMq7RaUgCoQedV.');

insert into user_roles (user_id, roles) values ('2', 'ADMIN');
