package com.dev5ops.healthtart.rival.service;


import com.dev5ops.healthtart.common.exception.CommonException;
import com.dev5ops.healthtart.common.exception.StatusEnum;
import com.dev5ops.healthtart.rival.domain.dto.RivalDTO;
import com.dev5ops.healthtart.rival.domain.dto.RivalUserInbodyDTO;
import com.dev5ops.healthtart.rival.domain.entity.Rival;
import com.dev5ops.healthtart.rival.repository.RivalRepository;
import com.dev5ops.healthtart.user.domain.CustomUserDetails;
import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import com.dev5ops.healthtart.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service("rivalService")
public class RivalServiceImpl implements RivalService {

    private final RivalRepository rivalRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Autowired
    public RivalServiceImpl(RivalRepository rivalRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.rivalRepository = rivalRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Override
    // 1. 내 라이벌 조회
    public RivalDTO findRivalMatch(){
        String userCode = getUserCode();
        return rivalRepository.findByUser_UserCode(userCode);
    }

    @Override
    // 2. 선택한 라이벌 조회 -> 내꺼하고 상대꺼 2개 보여줘야함. -> 결국 유저 정보가 필요하구나? user를 infra로 가져와야한다.
    public List<RivalUserInbodyDTO> findRival(String rivalUserCode){
        // 현재 인증된 사용자 가져오기
        String userCode = getUserCode();

        // 1단계: userCode와 rivalUserCode로 rivalMatchCode 가져오기
        Long rivalMatchCode = rivalRepository.findRivalMatchCode(userCode, rivalUserCode);

        if(rivalMatchCode == null){return null;} // 라이벌 코드가 없으면 안되게 해야함.

        // 2단계: userCode와 rivalUserCode 각각에 대한 인바디 + 유저 정보를 가져오기
        RivalUserInbodyDTO userInbodyDTO = rivalRepository.findUserInbodyByUserCode(userCode);
        RivalUserInbodyDTO rivalInbodyDTO = rivalRepository.findUserInbodyByUserCode(rivalUserCode);

        // 3단계: 가져온 DTO에 rivalMatchCode 추가
        userInbodyDTO.setRivalMatchCode(rivalMatchCode);
        rivalInbodyDTO.setRivalMatchCode(rivalMatchCode);

        return Arrays.asList(userInbodyDTO, rivalInbodyDTO);
    }

    @Override
    // 3. 라이벌 삭제 -> 라이벌 수정은 필요없을거같음. 라이벌 리스트에서 오른쪽에 삭제 버튼 만들어놓고 그걸 누르면 삭제되게 하는 로직으로 가자.
    // 그리고 삭제를 한다는게 flag를 바꾸는게 아닌거같음. user에만 flag를 만들어놓고 진행? -> 일단 하자.
    public void deleteRival(Long rivalMatchCode){

        Rival rival = rivalRepository.findById(rivalMatchCode)
                .orElseThrow(IllegalArgumentException::new); // 수정해주기
        log.info("Delete Rival");

        rivalRepository.delete(rival);
    }


    @Override
    // 4. 라이벌 등록 -> 랭킹 페이지에서 상세 페이지에 들어가면 "라이벌 등록"이 있으면 괜찮을듯? -> 결국 랭킹에서 userCode가 있을거임.(보이진 않지만) 그걸 토대로 하기?
    public RivalDTO insertRival(String rivalUserCode){
        // 어떤 데이터가 있으면 좋을까? -> 랭킹에는 그 사람의 userCode가 존재할게 분명. -> 그걸 프론트에서 받아와야함. -> 프론트의 랭킹에서 userEntity 정보를 미리 보여줘야함.
        String userCode = getUserCode();

        UserEntity user = userRepository.findById(userCode)
                        .orElseThrow(IllegalArgumentException::new);

        UserEntity rivalUser = userRepository.findById(rivalUserCode)
                        .orElseThrow(IllegalArgumentException::new);

        Rival rival = Rival.builder()
                .user(user)
                .rivalUser(rivalUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        rivalRepository.save(rival);

        return modelMapper.map(rival, RivalDTO.class);
    }


    public String getUserCode() {
        // 현재 인증된 사용자 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 인증된 사용자가 문자열(String)인 경우 (로그인하지 않은 상태)
        if (principal instanceof String) {
            throw new CommonException(StatusEnum.USER_NOT_FOUND);
        }

        // 인증된 사용자가 CustomUserDetails인 경우
        CustomUserDetails userDetails = (CustomUserDetails) principal;

        log.info("Authentication: {}", SecurityContextHolder.getContext().getAuthentication());
        log.info("userDetails: {}", userDetails.toString());

        // 현재 로그인한 유저의 유저코드 반환
        return userDetails.getUserDTO().getUserCode();
    }
}
