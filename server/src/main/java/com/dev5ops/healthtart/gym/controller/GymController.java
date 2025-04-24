package com.dev5ops.healthtart.gym.controller;

import com.dev5ops.healthtart.gym.domain.entity.Gym;
import com.dev5ops.healthtart.gym.domain.vo.request.RequestEditGymVO;
import com.dev5ops.healthtart.gym.domain.vo.request.RequestRegisterGymVO;
import com.dev5ops.healthtart.gym.domain.vo.response.ResponseEditGymVO;
import com.dev5ops.healthtart.gym.domain.vo.response.ResponseFindGymVO;
import com.dev5ops.healthtart.gym.domain.vo.response.ResponseGymInfoVO;
import com.dev5ops.healthtart.gym.domain.vo.response.ResponseRegisterGymVO;
import com.dev5ops.healthtart.gym.domain.dto.GymDTO;
import com.dev5ops.healthtart.gym.service.GymService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController("gymController")
@RequestMapping("gym")
@Slf4j
public class GymController {
    private final GymService gymService;
    private final ModelMapper modelMapper;

    @Autowired
    public GymController(GymService gymService, ModelMapper modelMapper) {
        this.gymService = gymService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "관리자 - 헬스장 등록")
    @PostMapping("/register")
    public ResponseEntity<ResponseRegisterGymVO> registerGym(@RequestBody RequestRegisterGymVO request) {
        GymDTO gymDTO = modelMapper.map(request, GymDTO.class);
        GymDTO registerGym = gymService.registerGym(gymDTO);

        ResponseRegisterGymVO response = new ResponseRegisterGymVO(
                registerGym.getGymCode(),
                registerGym.getGymName(),
                registerGym.getAddress(),
                registerGym.getBusinessNumber(),
                registerGym.getCreatedAt(),
                registerGym.getUpdatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "관리자 - 헬스장 정보 수정")
    @PatchMapping("/{gymCode}/edit")
    public ResponseEntity<ResponseEditGymVO> editGym(@PathVariable("gymCode") Long gymCode, @RequestBody RequestEditGymVO request) {
        GymDTO updatedGym = gymService.editGym(gymCode, request);
        ResponseEditGymVO response = new ResponseEditGymVO(
                updatedGym.getGymName(),
                updatedGym.getAddress(),
                updatedGym.getBusinessNumber(),
                updatedGym.getUpdatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "관리자 - 헬스장 정보 삭제")
    @DeleteMapping("/{gymCode}/delete")
    public ResponseEntity<String> deleteGym(@PathVariable("gymCode") Long gymCode) {
        gymService.deleteGym(gymCode);

        return ResponseEntity.ok("헬스장이 성공적으로 삭제되었습니다.");
    }

    @Operation(summary = "관리자, 유저 - 헬스장 단 건 조회")
    @GetMapping("/{gymCode}")
    public ResponseEntity<ResponseFindGymVO> getGym(@PathVariable("gymCode") Long gymCode) {
        GymDTO gymDTO = gymService.findGymByGymCode(gymCode);

        ResponseFindGymVO response = new ResponseFindGymVO(
                gymDTO.getGymName(),
                gymDTO.getAddress(),
                gymDTO.getBusinessNumber()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "관리자, 유저 - 헬스장 전체 조회")
    @GetMapping("/gym_list")
    public ResponseEntity<List<ResponseFindGymVO>> getGymList() {
        List<GymDTO> gymDTOList = gymService.findAllGym();
        List<ResponseFindGymVO> responseList = new ArrayList<>();

        for (GymDTO gymDTO : gymDTOList) {
            ResponseFindGymVO response = new ResponseFindGymVO(
                    gymDTO.getGymName(),
                    gymDTO.getAddress(),
                    gymDTO.getBusinessNumber()
            );

            responseList.add(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @Operation(summary = "사업자 등록 번호로 헬스장 조회")
    @GetMapping("/businessNumber/{businessNumber}")
    public ResponseEntity<ResponseGymInfoVO> getGymList(@PathVariable String businessNumber) {
        GymDTO gymDTO = gymService.findGymByBusinessCode(businessNumber);

        ResponseGymInfoVO response = new ResponseGymInfoVO(gymDTO.getGymCode(), gymDTO.getGymName());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
