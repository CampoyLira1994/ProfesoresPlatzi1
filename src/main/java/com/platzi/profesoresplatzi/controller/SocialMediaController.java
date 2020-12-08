package com.platzi.profesoresplatzi.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.platzi.profesoresplatzi.model.SocialMedia;
import com.platzi.profesoresplatzi.service.SocialMediaService;
import com.platzi.profesoresplatzi.util.CustomErrorType;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/v1")
public class SocialMediaController {
	
	@Autowired
	SocialMediaService _socialMediaService;
	
	//GET
	@RequestMapping(value="/socialMedias", method = RequestMethod.GET, headers = "Accept=application/json")
	public ResponseEntity<List<SocialMedia>> getSocialMedias(@RequestParam(value = "name", required = false)String name){
		
		List<SocialMedia> socialMedias = new ArrayList<>();
		
		if (name == null) {
			socialMedias = _socialMediaService.findAllSocialMedias();
			if (socialMedias.isEmpty()) {
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			}
			
			return new ResponseEntity<List<SocialMedia>>(socialMedias, HttpStatus.OK);	
		}else {
			SocialMedia socialMedia = _socialMediaService.findByName(name);
			if (socialMedia == null) {
				return new ResponseEntity(HttpStatus.NOT_FOUND);
			}
			
			socialMedias.add(socialMedia);
			return new ResponseEntity<List<SocialMedia>>(socialMedias, HttpStatus.OK);
		}
		
		
	}
	
	
	//GET
		@RequestMapping(value="/socialMedias/{id}",method= RequestMethod.GET,headers = "Accept=application/json")
		public ResponseEntity<SocialMedia> getSocialMediaById(@PathVariable("id")Long idSocialMedia){
			if (idSocialMedia == null|| idSocialMedia <=0) {
				return new ResponseEntity(new CustomErrorType("El idSocialmedia no es valido"),HttpStatus.CONFLICT);
			}
			SocialMedia socialMedia = _socialMediaService.findById(idSocialMedia);
			
			if (socialMedia == null) {
				return new ResponseEntity<SocialMedia>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<SocialMedia>(socialMedia,HttpStatus.OK);

		}
	
	
	
	
	//POST
	@RequestMapping(value="/socialMedias",method= RequestMethod.POST, headers = "Accept=application/json")
	public ResponseEntity<?> createSocialMedia(@RequestBody SocialMedia socialMedia,UriComponentsBuilder uriComponentsBuilder) {
		if (socialMedia.getName().equals(null)||socialMedia.getName().isEmpty()) {
			return new ResponseEntity(new CustomErrorType("SocialMedia name es requerido"),HttpStatus.CONFLICT);
		}
		
		if (_socialMediaService.findByName(socialMedia.getName()) != null) {
			return new  ResponseEntity(HttpStatus.NO_CONTENT);
		}
		
		_socialMediaService.saveSocialMedia(socialMedia);
		SocialMedia socialMedia2 = _socialMediaService.findByName(socialMedia.getName());
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(UriComponentsBuilder.fromPath("/v1/socialmedias/{id}")
				.buildAndExpand(socialMedia2.getIdSocialMedia())
				.toUri()
				);
		
		return new ResponseEntity<String>(headers,HttpStatus.CREATED);
	}
	
	//UPDATE
	@RequestMapping(value="/socialMedias/{id}",method= RequestMethod.PATCH)
	public ResponseEntity<SocialMedia> updateSocialMedia(@PathVariable("id") Long idSocialMedia,@RequestBody SocialMedia socialMedia){
		SocialMedia currentSocialMedia = _socialMediaService.findById(idSocialMedia);
		if (currentSocialMedia == null) {
			return new ResponseEntity(new CustomErrorType("isSocialMedia es requerido"),HttpStatus.CONFLICT);
		}
		
		currentSocialMedia.setName(socialMedia.getName());
		currentSocialMedia.setIcon(socialMedia.getIcon());
		
		_socialMediaService.updateSocialMedia(currentSocialMedia);
		return new ResponseEntity<SocialMedia>(currentSocialMedia,HttpStatus.OK);
	}

	
	//DELETE
	@RequestMapping(value="/socialMedias/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteSocialMedia(@PathVariable("id") Long idSocialMedia){
		if (idSocialMedia == null || idSocialMedia <= 0) {
			return new ResponseEntity(new CustomErrorType("idSocialMedia es requerido"),HttpStatus.CONFLICT);
		}
		
		SocialMedia socialMedia = _socialMediaService.findById(idSocialMedia);
		if (socialMedia == null) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		
		_socialMediaService.deleteSocialMediaById(idSocialMedia);
		return new ResponseEntity<SocialMedia>(HttpStatus.OK);
				
	}
	
	//CREATE IMAGE SOCIALMEDIAS
	//@RequestMapping(value="/socialMedias/images",method = RequestMethod.POST, headers = ("content-type=multipart/from-data"))
	///public  ResponseEntity<byte[]> uploadSocialMediaImage(@RequestParam("id_socialMedia")Long idSocialMedia
	//,@RequestParam("file")MultipartFile multipartFile
	//,UriComponentsBuilder componentsBuilder){
		
		//return new ResponseEntity<byte[]>,
	//}
	
}
