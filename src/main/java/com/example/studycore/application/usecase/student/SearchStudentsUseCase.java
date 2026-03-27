package com.example.studycore.application.usecase.student;

import com.example.studycore.application.usecase.student.output.SearchStudentOutput;
import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.port.StudentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchStudentsUseCase {

    private final StudentGateway studentGateway;

    public List<SearchStudentOutput> execute(final String q) {
        if (q == null || q.isBlank()) return List.of();

        final List<Student> students = studentGateway.searchByNameOrEmail(q.trim());
        return students.stream()
                .map(s -> new SearchStudentOutput(s.getId(), s.getName()))
                .toList();
    }
}

