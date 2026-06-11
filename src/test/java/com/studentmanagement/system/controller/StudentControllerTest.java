package com.studentmanagement.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentmanagement.system.dto.StudentRequest;
import com.studentmanagement.system.exception.GlobalExceptionHandler;
import com.studentmanagement.system.exception.ResourceNotFoundException;
import com.studentmanagement.system.model.Student;
import com.studentmanagement.system.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@Import(GlobalExceptionHandler.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @Test
    void shouldCreateStudent() throws Exception {
        Student student = student(1L, "Anurag", "anurag@example.com", "Computer Science", 20);
        when(studentService.createStudent(any(StudentRequest.class))).thenReturn(student);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request("Anurag", "anurag@example.com", "Computer Science", 20))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("anurag@example.com"));
    }

    @Test
    void shouldGetAllStudents() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of(
                student(1L, "A", "a@example.com", "CS", 19),
                student(2L, "B", "b@example.com", "Math", 21)
        ));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnNotFoundWhenStudentMissing() throws Exception {
        when(studentService.getStudentById(99L)).thenThrow(new ResourceNotFoundException("Student not found with id: 99"));

        mockMvc.perform(get("/api/students/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Student not found with id: 99"));
    }

    @Test
    void shouldUpdateStudent() throws Exception {
        Student updated = student(1L, "Updated", "updated@example.com", "Physics", 22);
        when(studentService.updateStudent(eq(1L), any(StudentRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request("Updated", "updated@example.com", "Physics", 22))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void shouldDeleteStudent() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnValidationErrorForInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request("", "bad-email", "", 1))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void shouldReturnBadRequestWhenDuplicateEmail() throws Exception {
        doThrow(new IllegalArgumentException("Email already exists: duplicate@example.com"))
                .when(studentService).createStudent(any(StudentRequest.class));

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request("Anurag", "duplicate@example.com", "CS", 20))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email already exists: duplicate@example.com"));
    }

    private static Student student(Long id, String name, String email, String course, Integer age) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setEmail(email);
        student.setCourse(course);
        student.setAge(age);
        return student;
    }

    private static StudentRequest request(String name, String email, String course, Integer age) {
        StudentRequest request = new StudentRequest();
        request.setName(name);
        request.setEmail(email);
        request.setCourse(course);
        request.setAge(age);
        return request;
    }
}
