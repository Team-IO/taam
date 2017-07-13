Unit testing in Minecraft is tricky.
Since most tests rely on an initialized Minecraft instance you basically have to start the whole game for the tests.

The way it is implemented here:
All unit tests are run with a bare essentials test framework that is called from within the game itself.
That means, the "test" source folder is compiled into the main mod as well.
Unit tests are invoked with the "Unit Tester" item.