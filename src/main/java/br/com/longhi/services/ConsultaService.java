package br.com.longhi.services;

import br.com.longhi.data.Consulta;
import br.com.longhi.repository.ConsultaRepository;
import br.com.longhi.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.dataprovider.EntryQuery;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    @Transactional(readOnly = true)
    public List<Entry> streamEntries(EntryQuery query) {
        var psi = authenticatedUser.carregarPsicologoLogado();

        return consultaRepository.findByPacientePsicologoAndDataBetween(
                psi,
                query.getStart().toLocalDate(),
                query.getEnd().toLocalDate()
        ).stream().map(Consulta::toEntry)
                .collect(Collectors.toList());
    }


    public Optional<Entry> getEntry(String entryId) {
        return consultaRepository
                .findById(Long.valueOf(entryId))
                .map(Consulta::toEntry);
    }

}
