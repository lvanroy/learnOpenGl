package com.example.learnOpenGl.gettingStarted.shaders;

import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static java.lang.System.exit;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ShaderMultiColorInterpolation {
    // Window size
    final static int width = 800;
    final static int height = 600;

    // Vertex shader
    private static final String VERTEX_SHADER =
            "#version 330 core\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "layout (location = 1) in vec3 aColor;\n" +
            "\n" +
            "out vec3 ourColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "  gl_Position = vec4(aPos, 1.0);\n" +
            "  ourColor = aColor;\n" +
            "}";

    // Fragment shader
    private static final String FRAGMENT_SHADER =
            "#version 330 core\n" +
            "in vec3 ourColor;\n" +
            "\n" +
            "out vec4 FragColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "  FragColor = vec4(ourColor, 1.0f);\n" +
            "}";

    // Define vertex input data
    private static final float[] VERTICES = {
            // Positions        // Colors
            -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, // Bottom right
             0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, // Bottom left
             0.0f,  0.5f, 0.0f, 0.0f, 0.0f, 1.0f  // Top
    };

    // Callbacks
    private static final GLFWFramebufferSizeCallbackI FRAMEBUFFER_SIZE_CALLBACK = (long window, int width, int height) -> glViewport(0, 0, width, height);

    // Process user input
    private static void processInput(long window) {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true);
        }
    }

    private static int createShader(int type, String implementation) {
        int shader = glCreateShader(type);
        glShaderSource(shader, implementation);
        glCompileShader(shader);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer success = stack.mallocInt(1);
            glGetShaderiv(shader, GL_COMPILE_STATUS, success);
            if (success.get(0) == GL_FALSE) {
                final String infoLog = glGetShaderInfoLog(shader);
                System.err.println("Shader linking error: " + infoLog);
            }
        }

        return shader;
    }

    private static int createProgram(int... shaders) {
        int program = glCreateProgram();
        for (int shader: shaders) {
            glAttachShader(program, shader);
        }
        glLinkProgram(program);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer success = stack.mallocInt(1);
            glGetProgramiv(program, GL_COMPILE_STATUS, success);
            if (success.get(0) == GL_FALSE) {
                final String infoLog = glGetProgramInfoLog(program);
                System.err.println("Program linking error: " + infoLog);
            }
        }

        return program;
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
        // Initialise the shader
        final int vertexShader = createShader(GL_VERTEX_SHADER, VERTEX_SHADER);
        final int fragmentShader = createShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER);

        // Create a shader program
        final int shaderProgram = createProgram(vertexShader, fragmentShader);
        glUseProgram(shaderProgram);

        // Clean up the shaders
        // They are no longer needed after compilation
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

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
            glUseProgram(shaderProgram);

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
