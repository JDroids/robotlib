package com.jdroids.robotlib.command

/**
 * This exception will be thrown if a command is used illegally. There are several ways for this to
 * happen.
 *
 * Essentially, a command is "locked" after it is first started or added to a [CommandGroup].
 *
 * This exception should be thrown if (after a command is locked) its requirements change, it is put
 * into multiple [CommandGroups][CommandGroup], it is started from outside its [CommandGroup], or the [CommandGroup]
 * adds a new child.
 */
class IllegalUseOfCommandException: RuntimeException {
    /**
     * Instantiates an [IllegalUseOfCommandException]
     */
    constructor()


    /**
     * Instantiates an [IllegalUseOfCommandException] with a given message
     *
     * @param message the message to instantiate with
     */
    constructor(message: String): super(message)


}