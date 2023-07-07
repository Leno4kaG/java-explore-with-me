CREATE TABLE IF NOT EXISTS stats (
 id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 app varchar NOT NULL,
 uri varchar NOT NULL,
 ip varchar NOT NULL,
 stats_time timestamp without time zone not null
);