We created the corresponding FSM from the Assignment sheet and ported it to UPPAAL.

This was done in the "CAS" template. Afterwards, for testing, we had to add a "USER" template which provided the CAS with random (nondeterministic) inputs.

We had to model the `SilentAndOpen` multiple times as else it would have not been possible to correctly model the `Car` and `Alarm` in two different templates.

For the verifier to work properly (because of the 10,000 different pin codes) we restricted the random values for the keypad to be 1230-1238 (with the correct pin being set to 1234). When one wants to use the actually correct model the select in keypad has to be adjusted such that MIN_PIN = 0 and MAX_PIN = 9999.

## Verifier
We created some Temporal Logical Constraints to verify that the model is indeed correct. 