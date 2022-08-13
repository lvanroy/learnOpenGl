package com.example.learnOpenGl.gettingStarted.helloTriangle;

import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static java.lang.System.exit;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Exercise3 {
    // Window size
    final static int width = 800;
    final static int height = 600;

    // Vertex shader
    private static final String VERTEX_SHADER =
            "#version 330 core\n" +
                    "layout (location = 0) in vec3 aPos;\n" +
                    "void main()\n" +
                    "{\n" +
                    "  gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" +
                    "}\0";

    // Fragment shader
    private static final String FRAGMENT_SHADER_ORANGE =
            "#version 330 core\n" +
                    "out vec4 FragColor;\n" +
                    "void main()\n" +
                    "{\n" +
                    "  FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n" +
                    "}";

    private static final String FRAGMENT_SHADER_YELLOW =
            "#version 330 core\n" +
                    "out vec4 FragColor;\n" +
                    "void main()\n" +
                    "{\n" +
                    "  FragColor = vec4(1.0f, 1.0f, 0.0f, 1.0f);\n" +
                    "}";

    // Define vertex input data
    private static final float[] VERTICES = {
            0.5f, 0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    private static final float[] VERTICES2 = {
            -0.5f, -0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            0.5f,  -0.5f, 0.0f
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

    private static void setUpVertexDataTriangle(int VAO, int VBO, float[] data) {
        // Bind the VAO first to ensure any further modifications are stored correctly
        glBindVertexArray(VAO);

        // Bind the VBO and set buffers
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

        // Configure vertex attributes
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, NULL);
        glEnableVertexAttribArray(0);

        // Reset the bind
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
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
        final int fragmentShaderOrange = createShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER_ORANGE);
        final int fragmentShaderYellow = createShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER_YELLOW);

        // Create a shader program
        final int shaderProgramOrange = createProgram(vertexShader, fragmentShaderOrange);
        final int shaderProgramYellow = createProgram(vertexShader, fragmentShaderYellow);

        // Clean up the shaders
        // They are no longer needed after compilation
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShaderOrange);
        glDeleteShader(fragmentShaderYellow);

        // Initialise the vertex data
        final int VAO = glGenVertexArrays();
        final int VBO = glGenBuffers();
        setUpVertexDataTriangle(VAO, VBO, VERTICES);

        final int VAO2 = glGenVertexArrays();
        final int VBO2 = glGenBuffers();
        setUpVertexDataTriangle(VAO2, VBO2, VERTICES2);

        // Set wireframe
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        // Render loop
        while(!glfwWindowShouldClose(window)) {
            // Input
            processInput(window);

            // Render
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            // Use the shader program
            glUseProgram(shaderProgramOrange);
            glBindVertexArray(VAO);
            glDrawArrays(GL_TRIANGLES, 0, 3);

            glUseProgram(shaderProgramYellow);
            glBindVertexArray(VAO2);
            glDrawArrays(GL_TRIANGLES, 0, 3);

            // Check and call events
            // Swap the buffers
            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        glfwTerminate();
    }
}
