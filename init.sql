CREATE TABLE IF NOT EXISTS users (
    id integer primary key,
    name varchar(10),
    last_name varchar(10),
    email varchar(30)
);

ALTER TABLE public.users REPLICA IDENTITY FULL;

INSERT INTO users (id,name,last_name,emaiL) VALUES(0,'Mario', 'Rossi', 'mario.rossi@gmail.com');
INSERT INTO users (id,name,last_name,emaiL) VALUES(1,'Luigi', 'Bianchi', 'luigi.bianchi@gmail.com');
INSERT INTO users (id,name,last_name,emaiL) VALUES(3,'Maurizio', 'Mauri', 'maurizio.mauri@gmail.com');
