CREATE TABLE IF NOT EXISTS users (
    id integer primary key,
    name varchar(10),
    last_name varchar(10),
    email varchar(30)
);

ALTER TABLE public.users REPLICA IDENTITY FULL;

INSERT INTO users (id,name,last_name,emaiL) VALUES(0,'mario', 'rossi', 'mario.rossi@gmail.com');
INSERT INTO users (id,name,last_name,emaiL) VALUES(1,'luigi', 'bianchi', 'luigi.bianchi@gmail.com');
INSERT INTO users (id,name,last_name,emaiL) VALUES(3,'maurizio', 'mauri', 'maurizio.mauri@gmail.com');
