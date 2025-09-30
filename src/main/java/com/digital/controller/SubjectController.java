// package com.digital.controller;

// import com.digital.dto.SubjectRequest;
// import com.digital.entity.Subject;
// import com.digital.servicei.SubjectService;
// import jakarta.validation.Valid;
// import lombok.AllArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @CrossOrigin("*")
// @RestController
// @RequestMapping(value = "/api/admin/subject")
// @AllArgsConstructor
// @PreAuthorize("hasRole('ADMIN')")
// public class SubjectController {

//     private final SubjectService subjectService;

//     /**
//      * API for ADMIN to add subject for class and teacher.
//      * */

//     @PostMapping(consumes = "application/json", produces = "text/plain")
//     public ResponseEntity<String> addSubject(@Valid @RequestBody SubjectRequest subjectRequest){
//         return new ResponseEntity<>(subjectService.addSubject(subjectRequest), HttpStatus.CREATED);
//     }

//     /**
//      * API for ADMIN to get subject by subject id.
//      * */

//     @GetMapping(path = "/{subjectId}", produces = "application/json")
//     public ResponseEntity<Subject> getSubject(@PathVariable Long subjectId){
//         return new ResponseEntity<>(subjectService.getSubject(subjectId), HttpStatus.OK);
//     }

//     /**
//      * API for ADMIN to get subjects by class id.
//      * */

//     @GetMapping(path = "/class/{classId}", produces = "application/json")
//     public ResponseEntity<List<Subject>> getSubjectsByClassId(@PathVariable Long classId){
//         return new ResponseEntity<>(subjectService.getSubjectsByClassId(classId), HttpStatus.OK);
//     }

//     /**
//      * API for ADMIN to get subjects by teacher id.
//      * */

//     @GetMapping(path = "/teacher/{id}", produces = "application/json")
//     public ResponseEntity<List<Subject>> getSubjectsByTeacherId(@PathVariable Long id){
//         return new ResponseEntity<>(subjectService.getSubjectsByTeacherId(id), HttpStatus.OK);
//     }

//     /**
//      * API for ADMIN to update subject.
//      * */

//     @PutMapping(path = "/update/{subjectId}", consumes = "multipart/form-data", produces = "text/plain")
//     public ResponseEntity<String> updateSubject(@PathVariable Long subjectId, @RequestParam Long teacherId){
//         return new ResponseEntity<>(subjectService.updateSubject(subjectId, teacherId), HttpStatus.OK);
//     }
// }
