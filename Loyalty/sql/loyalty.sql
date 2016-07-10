DROP TABLE loyalty_dynamusic_loyaltyTrasactions;
DROP TABLE loyalty_loyaltyTransaction;
commit work;

CREATE TABLE loyalty_loyaltyTransaction (
	id		VARCHAR(32)	not null,
	amount		INTEGER		not null,
	description	LONG VARCHAR	null,
	created		TIMESTAMP	not null,
	profile_id	VARCHAR(32)	not null,
	primary key(id)	
);

CREATE TABLE loyalty_dynamusic_loyaltyTrasactions (
	user_id			VARCHAR(32)	not null references dps_user(id),
	idx		        INTEGER		null,
	loyaltyTransaction_id	VARCHAR(32)	not null references loyalty_loyaltyTransaction(id),
	primary key(user_id, loyaltyTransaction_id)
);


commit work;

