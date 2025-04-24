package com.dev5ops.healthtart.inbody.service;

import com.dev5ops.healthtart.common.exception.CommonException;
import com.dev5ops.healthtart.common.exception.StatusEnum;
import com.dev5ops.healthtart.inbody.aggregate.Inbody;
import com.dev5ops.healthtart.inbody.aggregate.vo.request.RequestEditInbodyVO;
import com.dev5ops.healthtart.inbody.dto.FilterRequestDTO;
import com.dev5ops.healthtart.inbody.dto.InbodyDTO;
import com.dev5ops.healthtart.inbody.dto.InbodyUserDTO;
import com.dev5ops.healthtart.inbody.repository.InbodyRepository;
import com.dev5ops.healthtart.user.domain.UserTypeEnum;
import com.dev5ops.healthtart.user.domain.dto.UserDTO;
import com.dev5ops.healthtart.user.domain.entity.UserEntity;
import com.dev5ops.healthtart.user.repository.UserRepository;
import com.dev5ops.healthtart.user.service.UserService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service("inbodyService")
public class InbodyService {
    private final InbodyRepository inbodyRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Transactional
    public InbodyDTO registerInbody(InbodyDTO inbodyDTO) {
        log.info("인바디 등록 중: {}", inbodyDTO);
        inbodyDTO.setUserCode(inbodyDTO.getUser().getUserCode());

        UserDTO userDTO = userService.findById(inbodyDTO.getUserCode());
        if (userDTO == null) {
            log.warn("존재하지 않는 사용자: {}", inbodyDTO.getUserCode());
            throw new CommonException(StatusEnum.USER_NOT_FOUND);
        }
        log.info(userDTO.toString());

        UserEntity user = convertToUserEntity(userDTO);

        Inbody inbody = new Inbody();
        inbody.setInbodyScore(inbodyDTO.getInbodyScore());
        inbody.setWeight(inbodyDTO.getWeight());
        inbody.setHeight(inbodyDTO.getHeight());
        inbody.setMuscleWeight(inbodyDTO.getMuscleWeight());
        inbody.setFatWeight(inbodyDTO.getFatWeight());
        inbody.setBmi(inbodyDTO.getBmi());
        inbody.setFatPercentage(inbodyDTO.getFatPercentage());
        inbody.setDayOfInbody(inbodyDTO.getDayOfInbody());
        inbody.setBasalMetabolicRate(inbodyDTO.getBasalMetabolicRate());
        inbody.setUser(user);
        inbody.setCreatedAt(LocalDateTime.now());
        inbody.setUpdatedAt(LocalDateTime.now());

        Optional<Inbody> existingInbody = inbodyRepository.findByDayOfInbodyAndUser(inbodyDTO.getDayOfInbody(), user);
        if (existingInbody.isPresent()) {
            log.warn("중복 인바디 항목 발견: 날짜: {} 사용자: {}", inbodyDTO.getDayOfInbody(), user);
            throw new CommonException(StatusEnum.DAY_OF_INBODY_DUPLICATE);
        }

        log.info("데이터베이스에 인바디 저장 중: {}", inbody);
        Inbody savedInbody = inbodyRepository.save(inbody);
        log.info("저장된 인바디 객체: {}", savedInbody);

        return modelMapper.map(savedInbody, InbodyDTO.class);
    }

    @Transactional
    public InbodyDTO editInbody(Long inbodyCode, RequestEditInbodyVO request) {
        Inbody inbody = inbodyRepository.findById(inbodyCode).orElseThrow(() -> new CommonException(StatusEnum.INBODY_NOT_FOUND));

        inbody.setInbodyScore(request.getInbodyScore());
        inbody.setWeight(request.getWeight());
        inbody.setHeight(request.getHeight());
        inbody.setMuscleWeight(request.getMuscleWeight());
        inbody.setFatWeight(request.getFatWeight());
        inbody.setBmi(request.getBmi());
        inbody.setFatPercentage(request.getFatPercentage());
        inbody.setDayOfInbody(request.getDayOfInbody());
        inbody.setBasalMetabolicRate(request.getBasalMetabolicRate());
        inbody.setUpdatedAt(LocalDateTime.now());
        inbody.setUser(request.getUser());

        inbody = inbodyRepository.save(inbody);

        return modelMapper.map(inbody, InbodyDTO.class);
    }

    public void deleteInbody(Long inbodyCode) {
        Inbody inbody = inbodyRepository.findById(inbodyCode).orElseThrow(() -> new CommonException(StatusEnum.INBODY_NOT_FOUND));

        inbodyRepository.delete(inbody);
    }

    public InbodyDTO findInbodyByCode(Long inbodyCode) {
        Inbody inbody = inbodyRepository.findById(inbodyCode).orElseThrow(() -> new CommonException(StatusEnum.INBODY_NOT_FOUND));

        return modelMapper.map(inbody, InbodyDTO.class);
    }

    public List<InbodyDTO> findAllInbody() {
        List<Inbody> inbodyList = inbodyRepository.findAll();

        return inbodyList.stream()
                .map(inbody -> modelMapper.map(inbody, InbodyDTO.class))
                .collect(Collectors.toList());
    }

    public InbodyDTO findInbodyByCodeAndUser(Long inbodyCode, String userCode) {
        Inbody inbody = inbodyRepository.findByInbodyCodeAndUser_UserCode(inbodyCode, userCode)
                .orElseThrow(() -> new CommonException(StatusEnum.INBODY_NOT_FOUND));

        return modelMapper.map(inbody, InbodyDTO.class);
    }

    public List<InbodyDTO> findAllInbodyByUser(String userCode) {
        List<Inbody> inbodyList = inbodyRepository.findAllByUser_UserCode(userCode);
        if (inbodyList.isEmpty()) return new ArrayList<>();

        List<InbodyDTO> inbodyDTOList = new ArrayList<>();
        for (Inbody inbody : inbodyList) {
            InbodyDTO inbodyDTO = new InbodyDTO();

            inbodyDTO.setInbodyCode(inbody.getInbodyCode());
            inbodyDTO.setInbodyScore(inbody.getInbodyScore());
            inbodyDTO.setWeight(inbody.getWeight());
            inbodyDTO.setHeight(inbody.getHeight());
            inbodyDTO.setMuscleWeight(inbody.getMuscleWeight());
            inbodyDTO.setFatWeight(inbody.getFatWeight());
            inbodyDTO.setBmi(inbody.getBmi());
            inbodyDTO.setFatPercentage(inbody.getFatPercentage());
            inbodyDTO.setDayOfInbody(inbody.getDayOfInbody());
            inbodyDTO.setBasalMetabolicRate(inbody.getBasalMetabolicRate());
            inbodyDTO.setCreatedAt(inbody.getCreatedAt());
            inbodyDTO.setUpdatedAt(inbody.getUpdatedAt());

            if (inbody.getUser() != null) {
                UserEntity user = inbody.getUser();
                inbodyDTO.setUser(user);
                inbodyDTO.setUserCode(user.getUserCode());
            }

            inbodyDTOList.add(inbodyDTO);
        }

        return inbodyDTOList;
    }

    public List<InbodyUserDTO> findInbodyUserInbody() {
        return inbodyRepository.findLatestInbodyRankings();
    }

    public UserEntity convertToUserEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        return UserEntity.builder()
                .userCode(userDTO.getUserCode())
                .userType(UserTypeEnum.valueOf(userDTO.getUserType()))
                .userName(userDTO.getUserName())
                .userEmail(userDTO.getUserEmail())
                .userPassword(userDTO.getUserPassword())
                .userPhone(userDTO.getUserPhone())
                .userNickname(userDTO.getUserNickname())
                .userAddress(userDTO.getUserAddress())
                .userFlag(userDTO.getUserFlag())
                .userGender(userDTO.getUserGender())
                .userHeight(userDTO.getUserHeight())
                .userWeight(userDTO.getUserWeight())
                .userAge(userDTO.getUserAge())
                .createdAt(userDTO.getCreatedAt())
                .updatedAt(userDTO.getUpdatedAt())
                // gym 필드가 있다면 추가 매핑
                .build();
    }


    @Transactional
    public List<InbodyUserDTO> filterInbody(FilterRequestDTO filterRequest) {
        Specification<Inbody> spec = createSpecification(filterRequest);
        List<Inbody> inbodyList = inbodyRepository.findAll(spec);

        return inbodyList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.groupingBy(
                        InbodyUserDTO::getUserNickname,
                        Collectors.reducing((a, b) -> a.getInbodyScore() > b.getInbodyScore() ? a : b)
                ))
                .values()
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Specification<Inbody> createSpecification(FilterRequestDTO filterRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            addGenderPredicate(filterRequest, root, criteriaBuilder, predicates);
            addRangePredicate(filterRequest.getHeightRange(), "height", root, criteriaBuilder, predicates);
            addRangePredicate(filterRequest.getWeightRange(), "weight", root, criteriaBuilder, predicates);
            addRangePredicate(filterRequest.getMuscleWeightRange(), "muscleWeight", root, criteriaBuilder, predicates);
            addRangePredicate(filterRequest.getFatWeightRange(), "fatWeight", root, criteriaBuilder, predicates);
            addRangePredicate(filterRequest.getBmiRange(), "bmi", root, criteriaBuilder, predicates);
            addRangePredicate(filterRequest.getFatPercentageRange(), "fatPercentage", root, criteriaBuilder, predicates);
            addRangePredicate(filterRequest.getBasalMetabolicRateRange(), "basalMetabolicRate", root, criteriaBuilder, predicates);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addGenderPredicate(FilterRequestDTO filterRequest, Root<Inbody> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (filterRequest.getGender() != null) {
            predicates.add(criteriaBuilder.equal(root.get("user").get("userGender"), filterRequest.getGender()));
        }
    }

    private void addRangePredicate(FilterRequestDTO.Range range, String fieldName, Root<Inbody> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (range != null) {
            if (range.getMin() > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(fieldName), range.getMin()));
            }
            if (range.getMax() > 0) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(fieldName), range.getMax()));
            }
        }
    }

    private InbodyUserDTO convertToDTO(Inbody inbody) {
        return new InbodyUserDTO(
                inbody.getUser().getUserNickname(),
                inbody.getUser().getUserGender(),
                inbody.getHeight(),
                inbody.getWeight(),
                inbody.getMuscleWeight(),
                inbody.getFatPercentage(),
                inbody.getBasalMetabolicRate(),
                inbody.getInbodyScore()
        );
    }
}
