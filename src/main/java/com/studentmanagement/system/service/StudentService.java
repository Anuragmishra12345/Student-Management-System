package com.studentmanagement.system.service;

import com.studentmanagement.system.dto.StudentRequest;
import com.studentmanagement.system.exception.ResourceNotFoundException;
import com.studentmanagement.system.model.Student;
import com.studentmanagement.system.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    @Transactional
    public Student createStudent(StudentRequest request) {
        validateUniqueEmail(request.getEmail());

        Student student = mapRequestToStudent(request, new Student());
        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(Long id, StudentRequest request) {
        Student existingStudent = getStudentById(id);

        if (!existingStudent.getEmail().equalsIgnoreCase(request.getEmail())) {
            validateUniqueEmail(request.getEmail());
        }

        Student updatedStudent = mapRequestToStudent(request, existingStudent);
        return studentRepository.save(updatedStudent);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }

    private void validateUniqueEmail(String email) {
        if (studentRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
    }

    private Student mapRequestToStudent(StudentRequest request, Student student) {
        student.setName(request.getName().trim());
        student.setEmail(request.getEmail().trim().toLowerCase());
        student.setCourse(request.getCourse().trim());
        student.setAge(request.getAge());
        return student;
    }
}
