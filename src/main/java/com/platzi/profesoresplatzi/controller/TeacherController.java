package com.platzi.profesoresplatzi.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage.Body;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.platzi.profesoresplatzi.model.SocialMedia;
import com.platzi.profesoresplatzi.model.Teacher;
import com.platzi.profesoresplatzi.service.TeacherService;
import com.platzi.profesoresplatzi.util.CustomErrorType;

@Controller
@RequestMapping(value="/v1")
public class TeacherController {
	
	@Autowired
	private TeacherService _teacherService;
	
	//GET 
	@RequestMapping(value = "/teachers", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<List<Teacher>> getTeachers(@RequestParam(value="name", required=false) String name){
		List<Teacher> teachers = new ArrayList<Teacher>();
		
		if (name == null) {
			teachers = _teacherService.findAllTeachers();
	        if (teachers.isEmpty()) {
	            return new ResponseEntity(HttpStatus.NO_CONTENT);
	            // You many decide to return HttpStatus.NOT_FOUND
	        }
		   
			return new ResponseEntity<List<Teacher>>(teachers, HttpStatus.OK);
		} else {
			Teacher teacher = _teacherService.findByName(name);
			if (teacher == null) {
				return new ResponseEntity(HttpStatus.NOT_FOUND);
			}
			
			teachers.add(teacher);
			return new ResponseEntity<List<Teacher>>(teachers, HttpStatus.OK);
		}
		
		

    }
	
	//FIND BY ID
	@RequestMapping(value = "/teachers/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<Teacher> getCourseById(@PathVariable("id") Long id){
		Teacher teacher = _teacherService.findById(id);
        if (teacher == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<Teacher>(teacher, HttpStatus.OK);
    }
	
	//DELETE
	@RequestMapping(value = "/teachers/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<?> deleteCourse(@PathVariable("id") Long id) {
		Teacher teacher = _teacherService.findById(id);
        if (teacher == null) {
        	System.out.println("Unable to delete. teacher with id not found. " + id);
            
            return new ResponseEntity(new CustomErrorType("Unable to delete. teacher with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        
        _teacherService.deleteTeacherById(id);
        return new ResponseEntity<Teacher>(HttpStatus.NO_CONTENT);
    }
	
	public static final String TEACHER_UPLOADED_FOLDER = "C:/Spring_Tool_Suite/ProfesoresPlatzi/images/teachers/"; 
	
	//CREATE IMAGE TEACHER
	@RequestMapping(value="/teachers/images", method = RequestMethod.POST, headers = ("content-type=multipart/form-data"))
	public ResponseEntity<byte[]> uploadTeacherImage(@RequestParam("id_teacher")Long idTeacher
			,@RequestParam("file")MultipartFile multipartFile
			,UriComponentsBuilder componentsBuilder){
		
		if (idTeacher == null) {
			return new ResponseEntity(new CustomErrorType("Porfavor envia id_teacher"), HttpStatus.NO_CONTENT);
		}
		
		if (multipartFile.isEmpty()) {
			return new ResponseEntity(new CustomErrorType("Porfavor seleccione un archivo de carga"), HttpStatus.NO_CONTENT);
		}
		
		Teacher teacher = _teacherService.findById(idTeacher);
		if (teacher == null) {
			return new ResponseEntity(new CustomErrorType("El Teacher con id " +idTeacher+ " no encontrado"), HttpStatus.NO_CONTENT);
		}
		
		if (teacher.getAvatar().isEmpty()|| teacher.getAvatar() != null) {
			String fileName = teacher.getAvatar();
			Path path = Paths.get(fileName);
			File f = path.toFile();
			if (f.exists()) {
				f.delete();
			}
		}
		
		try {
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String dateName = dateFormat.format(date);
			
			String fileName = String.valueOf(idTeacher) + "-pictureTeacher-" + dateName + "."+ multipartFile.getContentType().split("/")[1];
			teacher.setAvatar(TEACHER_UPLOADED_FOLDER + fileName);
			
			byte[] bytes = multipartFile.getBytes();
			Path path = Paths.get(TEACHER_UPLOADED_FOLDER + fileName);
			Files.write(path, bytes);
			
			_teacherService.updateTeacher(teacher);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ResponseEntity(new CustomErrorType("Error en la carga de la imagen " + multipartFile.getOriginalFilename()), HttpStatus.NO_CONTENT);
		}
	}
	
	//GET IMAGE
	@RequestMapping(value = "/teachers/{id_teacher}/images", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getTeacherImage(@PathVariable("id_teacher")Long idTeacher){
		
        if (idTeacher == null) {
            // You many decide to return HttpStatus.NOT_FOUND
        	return new ResponseEntity(new CustomErrorType("Profavor agrega id_teacher "), HttpStatus.NO_CONTENT);
        }
        
        Teacher teacher = _teacherService.findById(idTeacher);
        
        if (teacher == null) {
        	return new ResponseEntity(new CustomErrorType("El profedor con id " + idTeacher + " no existe"), HttpStatus.NO_CONTENT);
		}
        
        try {
			String fileName = teacher.getAvatar();
			Path path = Paths.get(fileName);
			File f = path.toFile();
			if (!f.exists()) {
				return new ResponseEntity(new CustomErrorType("No se encontro la imagen"), HttpStatus.NO_CONTENT);
			}
			
			byte[] image = Files.readAllBytes(path);
        	return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ResponseEntity(new CustomErrorType("Error al mostrar la imagen"), HttpStatus.CONFLICT);
		}
	
    }
    
    //DELETE Image
	@RequestMapping(value = "/teachers/{id_teacher}/images", method = RequestMethod.DELETE,headers = "Accept=application/json")
    public ResponseEntity<?> deleteTeacherImage(@PathVariable("id_teacher")Long idTeacher){
		

        if (idTeacher == null) {
            // You many decide to return HttpStatus.NOT_FOUND
        	return new ResponseEntity(new CustomErrorType("Profavor agrega id_teacher "), HttpStatus.NO_CONTENT);
        }
        
        Teacher teacher = _teacherService.findById(idTeacher);
        if (teacher == null) {
        	return new ResponseEntity(new CustomErrorType("El profedor con id " + idTeacher + " no existe"), HttpStatus.NO_CONTENT);
		}
        
        if (teacher.getAvatar().isEmpty()||teacher.getAvatar() == null) {
        	return new ResponseEntity(new CustomErrorType("El teacher npo tiene una imagen asignada"), HttpStatus.NO_CONTENT);
		}
        
        String fileName = teacher.getAvatar();
        Path path = Paths.get(fileName);
        File file = path.toFile();
        if (file.exists()) {
			file.delete();		
        	}
        teacher.setAvatar("");
        _teacherService.updateTeacher(teacher);
        
        return new ResponseEntity<Teacher>(HttpStatus.OK);
	}
    
    
}
