package com.longhi.pms.services;

import com.longhi.pms.models.Consultorio;
import com.longhi.pms.models.HorarioAtendimento;
import com.longhi.pms.models.Psicologo;
import com.longhi.pms.models.Usuario;
import com.longhi.pms.repositories.ConsultorioRepository;
import com.longhi.pms.repositories.UsuarioRepository;
import com.longhi.pms.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultorioService {

    @Autowired
    private UsuarioRepository psicologoRepository;

    @Autowired
    private ConsultorioRepository consultorioRepository;

    public List<Consultorio> carregarConsultorios() {
        var psi = carregarPsicologoLogado();
        return consultorioRepository.findConsultorioByPsicologo((Psicologo) psi);
    }

    public void inserirConsultorio(Consultorio consultorio) {
        var psi = carregarPsicologoLogado();
        consultorio.setPsicologo((Psicologo) psi);
        consultorioRepository.save(consultorio);
    }

    public void removerConsultorio(Consultorio consultorio) {
        consultorioRepository.delete(consultorio);
    }

    private Usuario carregarPsicologoLogado() {
        var login = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return psicologoRepository.findPsicologoByLogin(login).get();
    }

    public void removerHorarioAtendimento(Consultorio consultorio, HorarioAtendimento horarioAtendimento) {
        var horarios = consultorio.getHorariosAtendimento().stream()
                .filter(h -> h != horarioAtendimento)
                .collect(Collectors.toList());
        consultorio.setHorariosAtendimento(horarios);

        consultorioRepository.save(consultorio);
    }
}
