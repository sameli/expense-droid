Expense Droid app:

Description: An Android app to manage financial transactions.

Current features;
* Multiple accounts
* Add/Delete/Update transactions
* Filter transactions based on date or amount

Planned features to add:
* Export/Import data
* Category selection for transactions
* Filter search for title
* Sort transactions

Technical specifications:
* Database consists of two tables. One for accounts and another for transactions

Personal notes:

TODO:
* select account in edit activity
* Fix style of action menu bar for android sdk < 21 (The filter menu doesn't turn green on sdk 19)
* Add help menu
* Add unit tests

TODO (less important):
* Add a reset database menu item


Features to skip for now:
* Category selection

Database basic fields:

id, title, amount, date, notes

Fields to add later:

category