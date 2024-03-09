# Changelog
<!--
This file is used to record all important changes for each release.
The format is based on https://keepachangelog.com/en/1.0.0/

## [vX.XX.X] - DD/MM/YYYY
### Added

### Changed

### Removed

### Fixed

### Chore

### Release notes

************** Copy and complete the template version and add categories to each release as needed **************
-->

## [vX.XX.X] - DD/MM/YYYY
### Added
* All UI **compose** elements for `GameFragment`
* New icons for game actions and a freeball icon
* `hilt` injection
* `dataStore` to `hilt`
* `changelog.md` file and populated it with all previous push and release notes

### Changed
* Converted navigation fragments `Rules`, `About` and `Improve` to **compose**
* Converted donate fragment to **compose**
* Refactored `Billing` class to make it compatible to the new fragment logic
* Converted `SettingsFragment` to **compose**
* Made logic changes to match toggles to reset `LiveData` on each settings click
* Converted app navigation, snackbar, appbar and drawer menu to **compose** for the whole app
* Separated composable styles into text, buttons and generic
* Converted `RulesFragment` to **compose**
* Converted `GameFragment` and `SummaryFragment` to **compose**
* Moved main game rules to `DataStore` and removed them from `SharedPreferences`
* Standardized a way to save to `DataStore` all match beginning setting changes, name changes and match toggles
* Removed `FrameToggles` class and moved the `FreeBall`, `LongShot` and `RestShot` toggles to the `Toggles` sealed class
* Rewrote the `MatchSettings` class to save each property into `DataStore` with every change
* Implemented **compose** functionality for `GenericDialog`
* Converted game score and actions to **compose** and refactored constants class
* Rearranged the project structure
* Replaced hard coded strings with constants
* Removed hard coded strings from database and created one `DAO` for each table
* Organized the existing **compose** screens and worked on the break screen
* Convert the `FoulDialog` to **compose** and added further implementation to `ActionMenu`
* Moved `addRed` and `removeColor` methods to the main ball buttons
* Converted the `ActionMenu` to **compose**
* Moved `FummaryFragment` to **compose**
* Moved `onBackPress` logic to **compose**
* Replaced all temporary icons with default Material Design icons
* Moved most toggles into `DataStore` and observed the flows from **compose** instead of launching events
* Refactored `MatchSettings` to fix leaks
* Replaced `DataStore` use within view models with `DataStoreRepository`
* Replaced the `DomainPlayer` sealed class with a new `DomainPlayer` data class to use this for player information all accross the app 

### Removed
* All xml files for navigation fragments
* All unused binding adapters
* Functionality from old `RulesFragment` and `MainActivity`
* Legacy code for `dialogs`
* Old `GameFragment`
* All xml layouts and unneeded gradle dependencies
* Last few unneeded xml drawables, shared preferences and classes

### Fixed
* `ActionMenu` from the `GameFragment`
* `AdMob` issues
* Game state logic for loading screens at start-up and for database fetching

### Chore
* Added **compose** dependencies to gradle
* Updated all dependencies and replace `kapt` with `ksp`

### Release notes
* Fully migrated the app to compose, rearranged project structure
* Added dependency injection with Hilt
* Implemented Data Store
* Refactored settings and player classes to fix leaks


## [v1.0.11] - 31/12/2022
### Added
* New icons for `LongShot`, `RestShot` and `FreeBall`
* Two extra toggles for showing `Advanced Statistics` and `Advanced Breaks`
* Columns for `Long Shots`, `Rest Shots` and `Points without Return`
* `Points without return` statistics: refactored database, added new shared prefs, refactored score and frame domain classes
* New action for `LAST_BLACK_FOUL` for edge snooker rule
* Database migration file to start incorporating migrations

### Changed
* Amended the `Foul Dialog`
* Moved `Remove Red` functionality to the `Foul Dialog` from the `Action Menu`
* Moved `Freeball` functionality to the `Foul Dialog`, refactored `FREEBALLINFO` to only take one variable `isActive`
* Added database columns for `Long Shots`, `Rest Shots` and for `Points without Return`; added DAO methods and calculated final scores to incorporate this
* Refactored separate extras in own layouts to be able to easily toggle them out

### Fixed
* `pointsWithoutReturn` bug
* Minor UI glitches

### Release notes
* Added remove reds and free ball functionality to the foul screen
* Added options to keep statistics on long shots and rest shots
* Added statistics for points without return
* Designed icons for long shots, rest shots and free ball toggles
* Added settings toggles to show/hide advanced statistics and advanced breaks
* Implemented logic for an edge rule of finishing the match when fouling on the last black


## [v1.0.10] - 05/12/2022
### Added
* `Force Retake` option in the `Foul Dialog` 
* Logic for forfeiting frame after 3 fouls in a row
* `Frame Handicap` and `Match Handicap` to `Rules`
* `Settings Fragment` to toggle the visibility of different match options
* New toggle for showing `Advanced Rules`; implemented and observed through matchVm; `Advanced Rules` from `RulesFragment` now hidden when this toggle is off
* Sliding toggle

### Changed
* Refactored `Break Rule` layouts into `Main` and `Advanced`, moved `Settings` updates to `RulesVm` from `MatchSettings`
* Added functionality to remember rules when already selected and app is restarted
* Added functionality to remember frame has started although there is no score, when app is restarted

### Fixed
* Rules saving and loading on start - added new matchState: `PENDING`, to remember selected rule before match has started
* Amended `Discard Action` and `Concede Match` to not be available when no frame is won

### Release notes
* Added new features: Force retake option, check for game forfeiting at 3 consecutive fouls, frame handicap, match handicap
* Added a new screen for settings
* Implemented a toggle button in the settings screen to show/hide advanced game rules for match setting up
* Fixed minor game logic glitches for conceding match
* Fixed glitch for remembering game rules selection when leaving app before starting new game
* Minor UI fixes for foul dialog, switch player animation, balls remaining, ad showing, splash screen, etc.


## [v1.0.9.1] - 02/12/2022
### Added
* Animation for switching players during the game

### Changed
* Amended `Snackbar` to hover above the bottom of the screen
* Made `Splash Screen` uniform across devices

### Fixed
* `Foul Info Dialog` opening issues
* `Balls Remaining` glitch
* Dialog crashes when clicking outside the dialog, `Discard Frame` glitches
* `AdMob` glitches

### Removed
* Suggested player names from release version

### Chore
* Renamed `PlayFragment` to `RulesFragment` and `PostGameFragment` to `SummaryFragment` and all associated variables

### Release notes
* Fixed minor glitches and made small UI changes


## [v1.0.9] - 01/12/2022
### Added
* Repository to `GameVm`, moved all frame logic from `MatchVm` for easier access to actions and consistency
* `BuildConfig` variable to hide ads in debug variant and a randomiser to show ads a certain percentage of the time
* Animation for buttons
* `Rules`, `Timber` list to issue log e-mail body

### Changed
* Replaced `Splash Screen` with a size that works on a range of phones
* Divided actions into queueable and non-queueable in case database is busy
* Refactored all classes for the new configuration; fragment view models all contain action live data now; this is stripped from `matchVm`. 
* Formatted test text logging, add further checks to score testing

### Removed
* Unnecessary scrolling from `Break View`
* Vertical padding in `NavDrawer Fragments`
* Text pop-up and vibrate on `Menu Item` long click
* `MatchVm` reference from `Foul Dialog` and `Generic Dialog`, which is now showed from `GameFragment` instead using `childManager` to get a `GameVm` reference
* `MatchVm` reference from `Play Rules` layout and is now created an `Update Rules` method in the `Play Fragment` instead
* Legacy xml fragments, recurring work, api services

### Fixed
* Dialog and database glitch on screen rotation
* `Foul Modifier` score glitch
* `Highest Score` glitch

### Chore
* Standardized all xml id and string names

### Release notes
* Fixed dialogs malfunctions on screen rotation
* Simplified background logic for better tracking of game actions
* Fixed database access overlaps
* Added factor variable to show ads only a percentage of the time
* Added buttons fade-in animation
* Fixed game glitches for foul modifier and highest score
* Removed legacy code and standardized naming
* Fixed minor UI glitches for balls list, splash screen, text appearance, etc.


## [v1.0.8] - 22/11/2022
### Added
* `Remove Color` functionality
* `Safe Miss` and `Snooker` to `End of Frame` check
* Wrote unit tests for `DomainBall` class
* `Log` button to save all log actions to db then send by e-mail
* New variable under `RULES` to add and increment unique `IDs` for each object required
* Fade in / fade out animation transitions between fragments
* Test for tracking all frame progression to easily detect game logic errors

### Changed
* Refactored domain classes to avoid overlapping checker method and moved each method to its corresponding class
* Refactored database to live save on each shot instead of `onSaveInstanceState`
* Refactored match saving / loading logic and `Shared Preferences`
* Refactored `Util` classes

### Removed
* Unused legacy `Bottom Navigation` and `Rankings` logic

### Fixed
* `Ballstack` and `Freeball` glitches
* Score bar glitches
* `Foul Dialog` closing glitches
* Various UI glitches
* `Ball` item binding adapter to show unclickable ball in `Break View`
* `Foul` ball display glitches and `Force Continue` button disabled display

### Release notes
* Added new features: Remove color, Safe miss and Snooker
* Implemented tests to check game logic
* Fixed UI and logic glitches: score bar, dialogs, ball view and others
* Added a log button to submit issues by e-mail
* Refactored database to save each shot in real time
* Refactored game classes and util classes
* Added fragment animation to fade in and out on transition
* Removed legacy unused fragments


## [v1.0.7] - 10/11/2022
### Added
* `adMob`
* Two extra buttons: `Safe Miss` and `Snooker`;
* New statistics for `Safety Success` and `Number of Snookers` and updated `RoomDb` accordingly
* All actions above to `Frame Breakdown`
* New button for `Miss`

### Fixed
* `Billing` pricing so it automatically updates with local currency
* `Splash Screen` theme errors
* Ripple effects for all balls, add selection effect for `Foul` balls

### Release notes
* Added interstitial ads
* Fixed app support price issues
* Added buttons for two new match tracking features: safe miss and snooker
* Added new button for missed shots in line with the rest of the balls
* Fixed UI glitches, add selection color change for fouls
* Added all actions to show in the frame breakdown


## [v1.0.6] - 05/11/2022
### Added
* `Billing` for in app purchases to enabled `Financial Support`

### Fixed
* Player switching glitch	

### Release notes
* Added functionality for Support the App screen	


## [v1.0.5] - 03/11/2022
### Added
* `Back` arrow to `DonateFragment`
* Small delay to `Freeball` button

### Changed
* Updated version descriptions
* Postponed main 3 fragments enter transitions until all elements have been drawn on screen
* Refactored `snackbar` strings
* Refactored `Play Fragment` to a `LinearLayout` and replaced `toasts` with `snackbars`. Introduced new `snackbar` options
* Refactored all icons to use extracted sizes and colours and added new colour for `no_ball`
* Refactored the app to call `SnookerBoardApplication.application()` instead of passing application as variable

### Fixed
* Beer icon
* `Db` saving & `Splash Screen` turn off glitches and added small delay to `Splash Screen` so that layout is fully created
* `PostGame` state and `Splash Screen` glitches

### Release notes
* Fixed UI screens overlapping
* Fixed DB saving glitches
* Replaced toasts with snackbars on main screen
* Added new ball colour for no ball
	

## [v1.0.4] - 31/10/2022
### Added
* `readme.md` file
* `AboutFragment` to add a list of all releases
* Link to user surveys for `ImproveFragment`

### Changed
* Split string resources into multiple files
* Renamed `AboutFragment` to `ImproveFragment`, 

### Chore
* Refactored all navigation strings and ids to maintain a consistent nav naming
  
### Release notes
* Added a readme file
* Added Improve screen and a dummy Support the app screen
* Changed About screen to show all previous versions
* Added user survey link
* Fixed match saving and loading glitches and minor UI glitches
	

## [v1.0.3] - 25/10/2022
### Added
* `Navigation Drawer`, consisting of `About` and `Rules` fragments
* `Splash Screen` and functionality to keep on until `view model` loads
* Functionality to get a `toast` message when clicking on the last ball on the table
* Dialog for re-spotting `Black Ball`

### Changed
* Styled toolbar buttons to white icons and text with tint opacity when disabled
* Refactored toolbar to use one main app toolbar as per best practice
* Refactored navigation so app has one starting point again, at `PlayFragment`. An if condition will facilitate navigation to other fragments depending on `RULES.state`
* Refactored `FrameEndingDialog` so it gets triggered only once, when the last ball is potted, from `gameVm`
* Refactored `gameVm` frame/match resetting conditions & player switching
* Refactored `Menu buttons` and `Action buttons` so that instead of disabling them the colour becomes inactive only and a snackbar gets called showing why the button is inactive
* Disabled `Back` pressing in all main fragments
* Refactored xml for `GameLayout`

### Removed
* `Continue Match` / `Cancel Current Match` functionality. No need. If player wants to cancel match can do so from menu
* `Game Break` layout

### Fixed
* `Info Foul Dialog` glitch
* Glitch to show number of `Red` balls remaining on the table
* Database save/load glitch
* `Break` scroll glitch, set to stack in reverse 

### Chore
* Upgraded Gradle libraries and sdk version
* Refactored navigation naming to be more compact

### Release notes
* Added splash screen
* Added navigation bar
* Added About and Rules screens
* Added conditions for action/menu buttons to show snack bar explaining when not selected
* Removed continue/cancel match screen
* Fixed match saving and loading glitches
* Fixed UI glitches such as scroll view


## [v1.0.2] - 14/10/2022
### Added
* Margin to balls in `GameFragment`
* Setting to manifest so that the screen stops readjusting when keyboard is on

### Changed
* Replaced `isMatchInProgress` with new state variable part of `RULES` class, to be able to add a post-match state and jump directly to statistics fragment when needed
* Refactored `gameVm` and `matchVm` so that score keeping exists only in `gameVm` and that mat`chVm deals only with repo, game state changes, and match actions
* Split extensions in two separate files, `sharedPrefs` now has its own file
* Disabled `Back pressed` option during game for now, it has no use
* Refactored `sharedPref` so that all rules are destroyed when a match is cancelled or finished, but are otherwise saved and loaded every time the app is loaded, even when `IDLE`
* Refactored logic so that a game is saved and continues even when no game action has been taken yet

### Removed
* `SharedPref` from `MatchVm`, no use

### Fixed
* `Undo` glitch on first action

### Chore
* Refactored `GameStatsFragment` to new name `PostGameFragment`

### Release notes
* Added functionality to open app straight onto match statistics when needed
* Fixed undo glitches, match saving glitches and UI glitches
	

## [v1.0.1] - 12/10/2022
### Added
* `BallType` enum to easier identify ball types
* Dialog for `Reset Frame` and `Reset Match`
* `Freeball` sealed class to deal with all visiblity/selection states and added it to `sharedPreferences`
* Functionality to check if a player is selected before start of game

### Changed
* Updated game menu
* Renamed match actions
* Updated general comments
* Simplified match start/load logic with `sharedPreferences`
* Moved action for `removeRed()` out of `Foul DialogFragment` and added it to the `GameFragment` menu
* Moved action for `freeBall()` out of the `Foul DialogFragment` and created a toggle which is only active after a foul
* Separated `ballStack` / `frameStack` / `freeball` actions
* Changed `toast` extension
* Refactored game side menu logic and fixed `addRed` and `removeRed` enabled condition
* Refactored `PlayViewModel` to use game `RULES` instead of multiple live data variables
* Refactored to use `RULES` to keep track of `crtPlayer` between fragments and viewModels
* Removed unnecessary enum for `FrameActions` and directed all actions to `handlePot()` and `handleUndo()` instead

### Removed
* Automatic player selection at the beginning of the game

### Fixed
* Game scrolling
* Name sequence
* `Rerack` enabled logic
* `Active player` screen background switch
* `Undo` glitches
* Android system warnings
* `Cancel` game glitches

### Release notes
* Added freeball toggle and removed it from foul dialog
* Implemented logic to check for game in progress and load a continuing match directly rather than always starting with the match settings screen
* Fixed undo glitches
* Fixed match action glitches and added new dialogs for major actions


## [v1.0.0] - 27/08/2022
### Added
* Initial release

### Release notes
* Initial Release
