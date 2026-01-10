We created the corresponding FSM from the Assignment sheet and ported it to UPPAAL.

This was done in the "CAS" template. Afterwards, for testing, we had to add a "USER" template which provided the CAS with random (nondeterministic) inputs.

We had to model the `SilentAndOpen` multiple times as else it would have not been possible to correctly model the `Car` and `Alarm` in two different templates.

For the verifier to work properly we restricted the random values for the keypad to be 1230-1238 (with the correct pin being set to 1234). When one wants to use the actually correct model the select in keypad has to be adjusted such that it correctly uses [PIN_MIN, PIN_MAX].

## Verifier
We created some Temporal Logical Constraints to verify that the model is indeed correct. 