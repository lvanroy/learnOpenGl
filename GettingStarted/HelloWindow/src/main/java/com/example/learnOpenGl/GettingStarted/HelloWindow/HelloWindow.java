package com.example.learnOpenGl.GettingStarted.HelloWindow;

import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import static java.lang.System.exit;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class HelloWindow {
    // Window size
    final static int width = 800;
    final static int height = 600;

    // Callbacks
    private static final GLFWFramebufferSizeCallbackI FRAMEBUFFER_SIZE_CALLBACK = (long window, int width, int height)-> {
        glViewport(0, 0, width, height);
    };

    // Process user input
    private static void processInput(long window) {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true);
        }
    }

    public static void main(String[] args) {
        // Initialize glfw window
        glfwInit();

        // Configure glfw using glfwWindowHint(option, value)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // Instantiate the GLFW window
        final long window = glfwCreateWindow(width, height, "LearnOpenGl", NULL, NULL);
        if (window == NULL) {
            System.out.println("Failed to create GLFW window");
            glfwTerminate();
            exit(-1);
        }

        // Make the window current. This is needed to load the OpenGL functions
        glfwMakeContextCurrent(window);

        // Load OpenGL functions
        try {
            GL.createCapabilities();
        } catch (IllegalStateException e) {
            System.out.println("Failed to initialize OpenGL");
            e.printStackTrace();
            System.out.println(e.getCause().getMessage());
            glfwTerminate();
            exit(-1);
        }

        glfwSetFramebufferSizeCallback(window, FRAMEBUFFER_SIZE_CALLBACK);

        // Render loop
        while(!glfwWindowShouldClose(window)) {
            // Input
            processInput(window);

            // Render
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            // Check and call events
            // Swap the buffers
            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        glfwTerminate();
    }
}
