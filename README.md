## Todo
- [X] cache fund data in local files
- [ ] integrate sqlite to cache funds data
- [ ] package as a desktop application
- [X] get assets by date
- [ ] draw asset change graph
- [ ] draw asset pie graph
- [X] asset fund operation by date
- [ ] support cash dividend
- [X] get operations by date range
- [X] show service fee
- [X] daily earnings
- [X] show net in/out
- [X] support operation type (all/in/out)
- [X] show operation asset cost price
- [ ] asset cost price for buy and sell in the same day
- [X] add test suite
- [ ] support operation add
- [ ] refactoring with unit test
- [X] show asset weightings
- [X] sort assert row by weightings descend
- [X] display net price with date
- [X] show total accumulated earnings
- [X] add support for convertible bond
- [X] add support for account tag
- [X] sort by code/weight 
- [X] show asset holds
- [ ] support multiple accounts
- [X] support asset allocation
- [X] export in csv format

## usage
```
./run.sh asset[.grid] [-s [weight|code]] [-o csv] [2021-09-30]
./run.sh chart week [2022-11-01 2022-11-30]
./run.sh operation 2022-11-01 2022-12-01 [all|in|out]
./run.sh allocation [-s [weight|hold]] [2023-08-11]
```
