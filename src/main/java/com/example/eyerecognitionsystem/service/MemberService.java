package com.example.eyerecognitionsystem.service;


import com.example.eyerecognitionsystem.entity.FullName;
import com.example.eyerecognitionsystem.entity.Member;
import com.example.eyerecognitionsystem.entity.Role;
import com.example.eyerecognitionsystem.repository.FullNameRepository;
import com.example.eyerecognitionsystem.repository.MemberRepository;
import com.example.eyerecognitionsystem.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FullNameRepository fullNameRepository;

    @Autowired
    private RoleRepository roleRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Integer id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy nhân viên với ID: " + id));
    }

    @Transactional
    public Member createMember(Member member) {
        // Lưu FullName trước nếu chưa có ID
        if (member.getFullName() != null && member.getFullName().getId() == null) {
            FullName savedFullName = fullNameRepository.save(member.getFullName());
            member.setFullName(savedFullName);
        }

        // Kiểm tra và lấy Role nếu chỉ có ID
        if (member.getRole() != null && member.getRole().getId() != null) {
            Optional<Role> roleOptional = roleRepository.findById(member.getRole().getId());
            if (roleOptional.isPresent()) {
                member.setRole(roleOptional.get());
            }
        }

        // Kiểm tra username đã tồn tại chưa
        if (memberRepository.existsByUsername(member.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại trong hệ thống");
        }

        // Kiểm tra email đã tồn tại chưa (nếu có)
        if (member.getEmail() != null && !member.getEmail().isEmpty() &&
                memberRepository.existsByEmail(member.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại trong hệ thống");
        }

        return memberRepository.save(member);
    }

    @Transactional
    public Member updateMember(Integer id, Member memberDetails) {
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy nhân viên với ID: " + id));

        // Cập nhật FullName
        if (memberDetails.getFullName() != null) {
            FullName fullName = existingMember.getFullName();
            if (fullName == null) {
                fullName = new FullName();
                fullNameRepository.save(fullName);
                existingMember.setFullName(fullName);
            }

            fullName.setFirstName(memberDetails.getFullName().getFirstName());
            fullName.setLastName(memberDetails.getFullName().getLastName());
            fullNameRepository.save(fullName);
        }

        // Cập nhật các thông tin khác
        if (memberDetails.getUsername() != null && !memberDetails.getUsername().equals(existingMember.getUsername())) {
            // Kiểm tra username mới đã tồn tại chưa
            if (memberRepository.existsByUsername(memberDetails.getUsername())) {
                throw new IllegalArgumentException("Username đã tồn tại trong hệ thống");
            }
            existingMember.setUsername(memberDetails.getUsername());
        }

        if (memberDetails.getPassword() != null && !memberDetails.getPassword().isEmpty()) {
            existingMember.setPassword(memberDetails.getPassword());
        }

        if (memberDetails.getEmail() != null && !memberDetails.getEmail().equals(existingMember.getEmail())) {
            // Kiểm tra email mới đã tồn tại chưa
            if (memberDetails.getEmail() != null && !memberDetails.getEmail().isEmpty() &&
                    memberRepository.existsByEmail(memberDetails.getEmail())) {
                throw new IllegalArgumentException("Email đã tồn tại trong hệ thống");
            }
            existingMember.setEmail(memberDetails.getEmail());
        }

        if (memberDetails.getPhoneNumber() != null) {
            existingMember.setPhoneNumber(memberDetails.getPhoneNumber());
        }

        if (memberDetails.getDepartment() != null) {
            existingMember.setDepartment(memberDetails.getDepartment());
        }

        // Cập nhật Role
        if (memberDetails.getRole() != null && memberDetails.getRole().getId() != null) {
            Role role = roleRepository.findById(memberDetails.getRole().getId())
                    .orElseThrow(() -> new NoSuchElementException("Không tìm thấy Role với ID: " + memberDetails.getRole().getId()));
            existingMember.setRole(role);
        }

        return memberRepository.save(existingMember);
    }

    @Transactional
    public void deleteMember(Integer id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy nhân viên với ID: " + id));

        memberRepository.delete(member);
    }

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy nhân viên với username: " + username));
    }
}
