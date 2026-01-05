//This file was generated from (Academic) UPPAAL 5.0.0 (rev. 714BA9DB36F49691), 2023-06-21

/*

*/
E<> car.OpenAndLocked

/*

*/
E<> car.OpenAndUnlocked

/*

*/
E<> car.ClosedAndUnlocked

/*

*/
A[] not deadlock

/*

*/
E<> car.ClosedAndLocked

/*

*/
E<> alarm.Armed

/*

*/
E<> alarm.FlashAndSound

/*

*/
E<> alarm.Flash

/*

*/
E<> car.SilentAndOpen

/*

*/
E<> (alarm.FlashAndSound and alarm.t == 30)

/*
Alarm may flash for the full 300 seconds
*/
E<> (alarm.Flash and alarm.t == 300)

/*

*/
A[] alarm.FlashAndSound imply !car.ClosedAndUnlocked
