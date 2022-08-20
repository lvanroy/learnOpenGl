package com.example.learnOpenGl.gettingStarted.transformations;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class CustomShader {
    private static final Logger logger = Logger.getAnonymousLogger();
    public final int program;

    CustomShader(URL vertexPath, URL fragmentPath) {
        // Create the shaders
        final int vertexShader = createShader(GL_VERTEX_SHADER, readFile(vertexPath));
        final int fragmentShader = createShader(GL_FRAGMENT_SHADER, readFile(fragmentPath));

        // Create the program and link the shaders
        program = createProgram(vertexShader, fragmentShader);

        // Clean up
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void use() {
        glUseProgram(program);
    }

    public void setBool(String name, boolean value) {
        glUniform1i(glGetUniformLocation(program, name), value ? 1 : 0);
    }

    public void setInt(String name, int value) {
        glUniform1i(glGetUniformLocation(program, name), value);
    }

    public void setFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(program, name), value);
    }

    public void setFloatMatrix4f(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer transformData = stack.mallocFloat(16);
            value.get(transformData);
            glUniformMatrix4fv(glGetUniformLocation(program, name), false, transformData);
        }
    }

    public void delete() {
        glDeleteProgram(program);
    }

    private String readFile(URL path) {
        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(path.openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }

        return builder.toString();
    }

    private int createProgram(int vertexShader, int fragmentShader) {
        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buffer = stack.mallocInt(1);
            glGetProgramiv(program, GL_LINK_STATUS, buffer);
            if (buffer.get(0) == GL_FALSE) {
                final String infoLog = glGetProgramInfoLog(program);
                logger.severe("Shader compilation failed: " + infoLog);
            }
        }

        return program;
    }

    private int createShader(int shaderType, String src) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, src);
        glCompileShader(shader);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buffer = stack.mallocInt(1);
            glGetShaderiv(shader, GL_COMPILE_STATUS, buffer);
            if (buffer.get(0) == GL_FALSE) {
                final String infoLog = glGetShaderInfoLog(shader);
                logger.severe("Shader compilation failed: " + infoLog);
            }
        }

        return shader;
    }
}
