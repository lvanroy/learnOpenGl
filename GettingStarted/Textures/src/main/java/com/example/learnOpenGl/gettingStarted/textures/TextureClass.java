package com.example.learnOpenGl.gettingStarted.textures;

import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.net.URL;
import java.nio.IntBuffer;

import static java.lang.System.exit;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memIntBuffer;

public class TextureClass {
    // Window size
    final static int width = 800;
    final static int height = 600;

    // Vertex and fragment shader
    private static final URL VERTEX_SHADER_PATH = TextureClass.class.getClassLoader().getResource("shader.vs");
    private static final URL FRAGMENT_SHADER_PATH = TextureClass.class.getClassLoader().getResource("shader.fs");

    // Define vertex input data
    private static final float[] VERTICES = {
            // Positions        // Colors
            -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, // Bottom left
             0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // Bottom right
             0.0f,  0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f  // Top
    };

    // Define texture input data
    private static final float[] TEXTURES = {
            0.0f, 0.0f, // Lower-left corner
            1.0f, 0.0f, // Lower-right corner
            0.5f, 1.0f  // Top-center corner
    };

    // Callbacks
    private static final GLFWFramebufferSizeCallbackI FRAMEBUFFER_SIZE_CALLBACK = (long window, int width, int height) -> glViewport(0, 0, width, height);

    // Process user input
    private static void processInput(long window) {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true);
        }
    }

    private static void loadTextures(String path) {
        int texture = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, texture);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.ints(0);
            IntBuffer height = stack.ints(0);
            IntBuffer nrChannels = stack.ints(0);
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

        // Initialise the framebuffer callback function
        glfwSetFramebufferSizeCallback(window, FRAMEBUFFER_SIZE_CALLBACK);

        // Create a shader program
        CustomShader shader = new CustomShader(VERTEX_SHADER_PATH, FRAGMENT_SHADER_PATH);
        shader.use();

        // Initialise the vertex data
        final int VBO = glGenBuffers();
        final int VAO = glGenVertexArrays();
        glBindVertexArray(VAO);
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, VERTICES, GL_STATIC_DRAW);

        // Feed the vertices to OpenGL
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, NULL);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // Reset the bind
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        // Render loop
        while(!glfwWindowShouldClose(window)) {
            // Input
            processInput(window);

            // Render
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            // Use the shader program
            shader.use();

            // Tech not needed as there is only one VBOe but that is not a realistic use case
            glBindVertexArray(VAO);
            glDrawArrays(GL_TRIANGLES, 0, 3);

            // Check and call events
            // Swap the buffers
            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        glfwTerminate();
    }
}
