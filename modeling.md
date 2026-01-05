We created the corresponding FSM from the Assignment sheet and ported it to UPPAAL.

This was done in the "CAS" template. Afterwards, for testing, we had to add a "USER" template which provided the CAS with random (nondeterministic) inputs.

## Verifier
We created some Temporal Logical Constraints to verify that the model is indeed correct. 

## Changes
When going from `SilentAndOpen` back to `Armed` we use the sync `arm!` instead of `close?`. Else, we would have had to add either multiple locations or another confusing channel for communication purposes. The other thing we could have done, was to remove the appraoch with multiple models and just model everything in one template, which probably would have been the nicest way when sticking to the original FSM. On the other hand, it would have become quite big and hard to understand one single big FSM.