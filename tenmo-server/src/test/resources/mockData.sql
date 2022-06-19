INSERT INTO tenmo_user (username, password_hash)
VALUES ('test', '$2a$10$xEMjLs3HZArjtiqF2P2Ks.PN7FA0T/AjYtBOyP7dQiqtPlZBFtosO'); --user_id will be 1001 due to sequence

INSERT INTO tenmo_user (username, password_hash)
VALUES ('trial', '$2a$10$RCodlLzwwbuMcnKHwCW3uOskp9Tgl85oDElefsDHhD.g5M1sE2/DO'); --user_id will be 1002 due to sequence

INSERT INTO account (user_id, balance)
VALUES (1001L, 1000.00);

INSERT INTO account(user_id, balance)
VALUES (1002L, 1000.00);

INSERT INTO transfer(transfer_type_id, transfer_status_id, account_from, account_to, amount)
VALUES (2, 2, 2001, 2002, 50.00);

INSERT INTO transfer(transfer_type_id, transfer_status_id, account_from, account_to, amount)
VALUES (2, 2, 2001, 2002, 45.00);

INSERT INTO transfer(transfer_type_id, transfer_status_id, account_from, account_to, amount)
VALUES (1, 1, 2001, 2002, 50.00);

INSERT INTO transfer(transfer_type_id, transfer_status_id, account_from, account_to, amount)
VALUES (1, 3, 2001, 2002, 50.00);