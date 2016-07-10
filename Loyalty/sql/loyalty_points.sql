DROP TABLE dcspp_loyalty_points;

commit work;

CREATE TABLE dcspp_loyalty_points (
	payment_group_id    VARCHAR(40)    NOT NULL REFERENCES dcspp_pay_group(payment_group_id),
        number_of_points    INTEGER        NOT NULL,
	user_id             VARCHAR(32)    NOT NULL,
        primary key(payment_group_id)
);

commit work;
