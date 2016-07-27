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
* Add an alert dialog for "delete" button action on the edit activity
* Fix style of toast from dialogs
* Fix style of "dialog_filter_date.xml"
* Refactor settings keys like "menu_filter_date_checkbox"
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