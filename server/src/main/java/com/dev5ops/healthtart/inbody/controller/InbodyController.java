package com.dev5ops.healthtart.inbody.controller;

import com.dev5ops.healthtart.common.exception.CommonException;
import com.dev5ops.healthtart.common.exception.StatusEnum;
import com.dev5ops.healthtart.inbody.aggregate.vo.request.RequestEditInbodyVO;
import com.dev5ops.healthtart.inbody.aggregate.vo.request.RequestRegisterInbodyVO;
import com.dev5ops.healthtart.inbody.aggregate.vo.response.ResponseInbodyUserVO;
import com.dev5ops.healthtart.inbody.aggregate.vo.response.ResponseEditInbodyVO;
import com.dev5ops.healthtart.inbody.aggregate.vo.response.ResponseFindInbodyVO;
import com.dev5ops.healthtart.inbody.aggregate.vo.response.ResponseRegisterInbodyVO;
import com.dev5ops.healthtart.inbody.dto.FilterRequestDTO;
import com.dev5ops.healthtart.inbody.dto.InbodyDTO;
import com.dev5ops.healthtart.inbody.dto.InbodyUserDTO;
import com.dev5ops.healthtart.inbody.service.InbodyService;
import com.dev5ops.healthtart.user.domain.dto.UserDTO;
import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import com.dev5ops.healthtart.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController("inbodyController")
@RequestMapping("inbody")
@Slf4j
public class InbodyController {
    private final InbodyService inbodyService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public InbodyController(InbodyService inbodyService, UserService userService, ModelMapper modelMapper) {
        this.inbodyService = inbodyService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "관리자, 유저 - 인바디 등록")
    @PostMapping("/register")
    public ResponseEntity<?> registerInbody(@RequestBody RequestRegisterInbodyVO request) {
        log.info("인바디 등록 요청 : {}", request);
        try {
            UserDTO userDTO = userService.findById(request.getUserCode());
            if (userDTO == null) {
                log.warn("존재하지 않는 사용자: {}", request.getUserCode());
                throw new CommonException(StatusEnum.USER_NOT_FOUND);
            }

            UserEntity user = modelMapper.map(userDTO, UserEntity.class);

            InbodyDTO inbodyDTO = new InbodyDTO();
            inbodyDTO.setInbodyScore(request.getInbodyScore());
            inbodyDTO.setWeight(request.getWeight());
            inbodyDTO.setHeight(request.getHeight());
            inbodyDTO.setMuscleWeight(request.getMuscleWeight());
            inbodyDTO.setFatWeight(request.getFatWeight());
            inbodyDTO.setBmi(request.getBmi());
            inbodyDTO.setFatPercentage(request.getFatPercentage());
            inbodyDTO.setDayOfInbody(request.getDayOfInbody());
            inbodyDTO.setBasalMetabolicRate(request.getBasalMetabolicRate());
            inbodyDTO.setUser(user);

            InbodyDTO registerInbody = inbodyService.registerInbody(inbodyDTO);

            log.info("인바디 등록 성공: {}", registerInbody);
            return ResponseEntity.status(HttpStatus.CREATED).body(registerInbody);
        } catch (CommonException e) {
            log.error("인바디 등록 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("예상치 못한 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예상치 못한 오류가 발생했습니다");
        }
    }

    @Operation(summary = "관리자, 유저 - 인바디 수정")
    @PatchMapping("/{inbodyCode}/edit")
    public ResponseEntity<ResponseEditInbodyVO> editInbody(@PathVariable("inbodyCode") Long inbodyCode, @RequestBody RequestEditInbodyVO request) {
        InbodyDTO inbodyDTO = inbodyService.editInbody(inbodyCode, request);

        ResponseEditInbodyVO response = new ResponseEditInbodyVO(
                inbodyDTO.getInbodyScore(),
                inbodyDTO.getWeight(),
                inbodyDTO.getHeight(),
                inbodyDTO.getMuscleWeight(),
                inbodyDTO.getFatWeight(),
                inbodyDTO.getBmi(),
                inbodyDTO.getFatPercentage(),
                inbodyDTO.getDayOfInbody(),
                inbodyDTO.getBasalMetabolicRate(),
                inbodyDTO.getUpdatedAt(),
                inbodyDTO.getUser()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "관리자, 유저 - 인바디 삭제")
    @DeleteMapping("/{inbodyCode}")
    public ResponseEntity<String> deleteInbody(@PathVariable("inbodyCode") Long inbodyCode) {
        inbodyService.deleteInbody(inbodyCode);

        return ResponseEntity.ok("인바디 정보가 성공적으로 삭제되었습니다.");
    }

    @Operation(summary = "관리자 - 인바디 단 건 조회")
    @GetMapping("/{inbodyCode}")
    public ResponseEntity<ResponseFindInbodyVO> getInbody(@PathVariable("inbodyCode") Long inbodyCode) {
        InbodyDTO inbodyDTO = inbodyService.findInbodyByCode(inbodyCode);

        ResponseFindInbodyVO response = new ResponseFindInbodyVO(
                inbodyDTO.getInbodyScore(),
                inbodyDTO.getWeight(),
                inbodyDTO.getHeight(),
                inbodyDTO.getMuscleWeight(),
                inbodyDTO.getFatWeight(),
                inbodyDTO.getBmi(),
                inbodyDTO.getFatPercentage(),
                inbodyDTO.getDayOfInbody(),
                inbodyDTO.getBasalMetabolicRate()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "관리자 - 인바디 전체 조회")
    @GetMapping("/inbody_list")
    public ResponseEntity<List<ResponseFindInbodyVO>> getInbodyList() {
        List<InbodyDTO> inbodyDTOList = inbodyService.findAllInbody();
        List<ResponseFindInbodyVO> responseList = new ArrayList<>();

        for (InbodyDTO inbodyDTO : inbodyDTOList) {
            ResponseFindInbodyVO response = new ResponseFindInbodyVO(
                    inbodyDTO.getInbodyScore(),
                    inbodyDTO.getWeight(),
                    inbodyDTO.getHeight(),
                    inbodyDTO.getMuscleWeight(),
                    inbodyDTO.getFatWeight(),
                    inbodyDTO.getBmi(),
                    inbodyDTO.getFatPercentage(),
                    inbodyDTO.getDayOfInbody(),
                    inbodyDTO.getBasalMetabolicRate()
            );

            responseList.add(response);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/inbody_ranking")
    public ResponseEntity<List<ResponseInbodyUserVO>> findInbodyUserInbody(){
        List<InbodyUserDTO> AllInbodyList = inbodyService.findInbodyUserInbody();
        List<ResponseInbodyUserVO> response = new ArrayList<>();
        for (InbodyUserDTO inbodyUserDTO : AllInbodyList) {
            ResponseInbodyUserVO responseInbodyUserVO = new ResponseInbodyUserVO(
                    inbodyUserDTO.getUserNickname(),
                    inbodyUserDTO.getUserGender(),
                    inbodyUserDTO.getHeight(),
                    inbodyUserDTO.getWeight(),
                    inbodyUserDTO.getMuscleWeight(),
                    inbodyUserDTO.getFatPercentage(),
                    inbodyUserDTO.getBasalMetabolicRate(),
                    inbodyUserDTO.getInbodyScore()
            );
            response.add(responseInbodyUserVO);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "유저 - 본인의 인바디 단 건 조회")
    @GetMapping("/my-inbody/{inbodyCode}")
    public ResponseEntity<ResponseFindInbodyVO> getUserInbody(@PathVariable("inbodyCode") Long inbodyCode, @RequestParam("userCode") String userCode) {
        InbodyDTO inbodyDTO = inbodyService.findInbodyByCodeAndUser(inbodyCode, userCode);

        ResponseFindInbodyVO response = new ResponseFindInbodyVO(
                inbodyDTO.getInbodyScore(),
                inbodyDTO.getWeight(),
                inbodyDTO.getHeight(),
                inbodyDTO.getMuscleWeight(),
                inbodyDTO.getFatWeight(),
                inbodyDTO.getBmi(),
                inbodyDTO.getFatPercentage(),
                inbodyDTO.getDayOfInbody(),
                inbodyDTO.getBasalMetabolicRate()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "유저 - 본인의 모든 인바디 정보 조회")
    @GetMapping("/my-inbody")
    public ResponseEntity<List<ResponseFindInbodyVO>> getUserInbodyList(@RequestParam("userCode") String userCode) {
        List<InbodyDTO> inbodyDTOList = inbodyService.findAllInbodyByUser(userCode);
        List<ResponseFindInbodyVO> responseList = new ArrayList<>();

        for (InbodyDTO inbodyDTO : inbodyDTOList) {
            ResponseFindInbodyVO response = new ResponseFindInbodyVO(
                    inbodyDTO.getInbodyScore(),
                    inbodyDTO.getWeight(),
                    inbodyDTO.getHeight(),
                    inbodyDTO.getMuscleWeight(),
                    inbodyDTO.getFatWeight(),
                    inbodyDTO.getBmi(),
                    inbodyDTO.getFatPercentage(),
                    inbodyDTO.getDayOfInbody(),
                    inbodyDTO.getBasalMetabolicRate()
            );
            responseList.add(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @Operation(summary = "유저 - 인바디 필터링")
    @PostMapping("/filter")
    public ResponseEntity<List<ResponseInbodyUserVO>> filterInbody(@RequestBody FilterRequestDTO filterRequest) {
        List<InbodyUserDTO> filteredInbodyList = inbodyService.filterInbody(filterRequest);
        List<ResponseInbodyUserVO> response = new ArrayList<>();

        for (InbodyUserDTO inbodyUserDTO : filteredInbodyList) {
            ResponseInbodyUserVO responseInbodyUserVO = new ResponseInbodyUserVO(
                    inbodyUserDTO.getUserNickname(),
                    inbodyUserDTO.getUserGender(),
                    inbodyUserDTO.getHeight(),
                    inbodyUserDTO.getWeight(),
                    inbodyUserDTO.getMuscleWeight(),
                    inbodyUserDTO.getFatPercentage(),
                    inbodyUserDTO.getBasalMetabolicRate(),
                    inbodyUserDTO.getInbodyScore()
            );
            response.add(responseInbodyUserVO);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
