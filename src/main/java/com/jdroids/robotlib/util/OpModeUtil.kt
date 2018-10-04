package com.jdroids.robotlib.util

import android.app.Activity
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.HardwareDevice
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl
import org.firstinspires.ftc.robotcore.internal.system.AppUtil

fun getActiveOpMode(): OpMode = getActiveOpModeManagerImpl().activeOpMode

fun getActiveOpModeManagerImpl(): OpModeManagerImpl = getActiveOpModeManagerImplFromActivity(
        AppUtil.getInstance().activity)

private fun getActiveOpModeManagerImplFromActivity(activity: Activity): OpModeManagerImpl =
        OpModeManagerImpl.getOpModeManagerOfActivity(activity)

fun getNameOfHardwareDevice(device: HardwareDevice): String =
        getActiveOpMode().hardwareMap.getNamesOf(device).iterator().next()