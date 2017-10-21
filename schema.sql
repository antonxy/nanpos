--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.4
-- Dumped by pg_dump version 10.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: products; Type: TABLE; Schema: public; Owner: nanpos
--

CREATE TABLE products (
    id integer NOT NULL,
    name character varying(50) NOT NULL,
    ean bigint,
    price integer NOT NULL,
    visible boolean DEFAULT true NOT NULL
);


ALTER TABLE products OWNER TO nanpos;

--
-- Name: products_id_seq; Type: SEQUENCE; Schema: public; Owner: nanpos
--

CREATE SEQUENCE products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE products_id_seq OWNER TO nanpos;

--
-- Name: products_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nanpos
--

ALTER SEQUENCE products_id_seq OWNED BY products.id;


--
-- Name: revenues; Type: TABLE; Schema: public; Owner: nanpos
--

CREATE TABLE revenues (
    id integer NOT NULL,
    "user" integer NOT NULL,
    product integer NOT NULL,
    amount integer NOT NULL
);


ALTER TABLE revenues OWNER TO nanpos;

--
-- Name: revenues_id_seq; Type: SEQUENCE; Schema: public; Owner: nanpos
--

CREATE SEQUENCE revenues_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE revenues_id_seq OWNER TO nanpos;

--
-- Name: revenues_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nanpos
--

ALTER SEQUENCE revenues_id_seq OWNED BY revenues.id;


--
-- Name: revenues_product_seq; Type: SEQUENCE; Schema: public; Owner: nanpos
--

CREATE SEQUENCE revenues_product_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE revenues_product_seq OWNER TO nanpos;

--
-- Name: revenues_product_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nanpos
--

ALTER SEQUENCE revenues_product_seq OWNED BY revenues.product;


--
-- Name: revenues_user_seq; Type: SEQUENCE; Schema: public; Owner: nanpos
--

CREATE SEQUENCE revenues_user_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE revenues_user_seq OWNER TO nanpos;

--
-- Name: revenues_user_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nanpos
--

ALTER SEQUENCE revenues_user_seq OWNED BY revenues."user";


--
-- Name: users; Type: TABLE; Schema: public; Owner: nanpos
--

CREATE TABLE users (
    id integer NOT NULL,
    name character varying(200) NOT NULL,
    card character varying(200),
    isop boolean DEFAULT false NOT NULL,
    pin integer
);


ALTER TABLE users OWNER TO nanpos;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: nanpos
--

CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE users_id_seq OWNER TO nanpos;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: nanpos
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- Name: products id; Type: DEFAULT; Schema: public; Owner: nanpos
--

ALTER TABLE ONLY products ALTER COLUMN id SET DEFAULT nextval('products_id_seq'::regclass);


--
-- Name: revenues id; Type: DEFAULT; Schema: public; Owner: nanpos
--

ALTER TABLE ONLY revenues ALTER COLUMN id SET DEFAULT nextval('revenues_id_seq'::regclass);


--
-- Name: revenues user; Type: DEFAULT; Schema: public; Owner: nanpos
--

ALTER TABLE ONLY revenues ALTER COLUMN "user" SET DEFAULT nextval('revenues_user_seq'::regclass);


--
-- Name: revenues product; Type: DEFAULT; Schema: public; Owner: nanpos
--

ALTER TABLE ONLY revenues ALTER COLUMN product SET DEFAULT nextval('revenues_product_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: nanpos
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: nanpos
--

ALTER TABLE ONLY products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


--
-- Name: revenues revenues_pkey; Type: CONSTRAINT; Schema: public; Owner: nanpos
--

ALTER TABLE ONLY revenues
    ADD CONSTRAINT revenues_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: nanpos
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: revenues revenues_products_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: nanpos
--

ALTER TABLE ONLY revenues
    ADD CONSTRAINT revenues_products_id_fk FOREIGN KEY (product) REFERENCES products(id) ON DELETE SET NULL;


--
-- Name: revenues revenues_users_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: nanpos
--

ALTER TABLE ONLY revenues
    ADD CONSTRAINT revenues_users_id_fk FOREIGN KEY ("user") REFERENCES users(id);


--
-- PostgreSQL database dump complete
--

