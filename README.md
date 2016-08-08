## Description
Expense Droid is an easy to use app to manage financial transactions. It can be used for inventories or personal transactions.
Some of the features of this app:
- Support multiple accounts
- Filter transactions based on date or amount
- Simple interface to modify transactions


## Installation
Available on Google Play: https://play.google.com/store/apps/details?id=com.expensedroid.expensedroid

## Compile from source
Note: The script below has been tested on Ubuntu 14.04 and Android SDK tools 25.1.7:

```bash
git clone https://github.com/sameli/expense-droid.git
cd expense-droid
# Then open the folder in Android Studio and build the project. Or you can also use command line:
export ANDROID_HOME=/path/to/your/android/sdk
./gradlew assembleRelease
# The generated apk should be in this path:
./build/outputs/apk/app-release-unsigned.apk
```

## Planned features to add
* Export/Import data
* Category selection for transactions
* Filter search for title
* Sort transactions
* Photo field for transactions (Add a "take photo" button to the edit activity)
* Chart transactions based on type or date (weekly, monthly or yearly depending on the size of data)
* Group view based on day, week, month and year

## TODO

* Modify edit activity so users can move a transaction from one account to another
* Fix style of action menu bar for android sdk < 21 (The filter menu doesn't turn green on sdk 19)
* Add help menu
* Add unit tests
