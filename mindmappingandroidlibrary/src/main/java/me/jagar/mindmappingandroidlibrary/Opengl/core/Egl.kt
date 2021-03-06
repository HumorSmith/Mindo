package com.otaliastudios.opengl.core


import android.annotation.TargetApi
import android.opengl.EGL14
import android.opengl.GLES20
import android.opengl.GLU
import android.opengl.Matrix
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


fun FloatArray.makeIdentity(): FloatArray {
    if (size != 16) throw RuntimeException("Need a 16 values matrix.")
    Matrix.setIdentityM(this, 0)
    return this
}

object Egl {

    // Identity matrix for general use.
    @JvmStatic
    val IDENTITY_MATRIX = FloatArray(16)

    init {
        IDENTITY_MATRIX.makeIdentity()
    }

    // Allocates a direct float buffer, and populates it with the float array data.
    // Allocate a direct ByteBuffer, using 4 bytes per float, and copy coords into it.
    internal fun floatBufferOf(coords: FloatArray): FloatBuffer {
        val bb = ByteBuffer.allocateDirect(coords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        val fb = bb.asFloatBuffer()
        setFloatBuffer(fb, coords)
        return fb
    }

    internal fun setFloatBuffer(buffer: FloatBuffer, coords: FloatArray) {
        buffer.put(coords)
        buffer.position(0)
    }

    /**
     * Checks for GLES errors.
     */
    @JvmStatic
    fun check(opName: String) {
        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            val message = "Error during $opName: glError 0x${Integer.toHexString(error)}: ${GLU.gluErrorString(error)}"
            Log.e("Egl", message)
            throw RuntimeException(message)
        }
    }

    /**
     * Checks for EGL errors.
     */
    @JvmStatic
    fun checkEgl(eglOpName: String) {
        val error = EGL14.eglGetError()
        if (error != EGL14.EGL_SUCCESS) {
            val message = "Error during $eglOpName: EGL error 0x${Integer.toHexString(error)}"
            Log.e("Egl", message)
            throw RuntimeException(message)
        }
    }

    // Check for valid location.
    @JvmStatic
    fun checkLocation(location: Int, label: String) {
        if (location < 0) {
            val message = "Unable to locate $label in program"
            Log.e("Egl", message)
            throw RuntimeException(message)
        }
    }
}