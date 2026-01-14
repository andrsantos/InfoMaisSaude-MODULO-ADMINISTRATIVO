package com.Projeto.InfoMaisSaude.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaCreateDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaResponseCreateDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaResponseDeleteDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaResponseReadDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaResponseUpdateDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaResumoDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaUpdateDTO;
import com.Projeto.InfoMaisSaude.entities.Clinica;
import com.Projeto.InfoMaisSaude.entities.Medico;
import com.Projeto.InfoMaisSaude.entities.Usuario;
import com.Projeto.InfoMaisSaude.enums.UserRole;
import com.Projeto.InfoMaisSaude.exceptions.ClinicaJaExisteException;
import com.Projeto.InfoMaisSaude.exceptions.ClinicaNaoExisteException;
import com.Projeto.InfoMaisSaude.exceptions.PermissaoException;
import com.Projeto.InfoMaisSaude.repositories.ClinicaRepository;
import com.Projeto.InfoMaisSaude.repositories.MedicosRepository;
import com.Projeto.InfoMaisSaude.repositories.UsuariosRepository;
import com.Projeto.InfoMaisSaude.services.ClinicaService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ClinicaServiceImpl implements ClinicaService {

    @Autowired
    private ClinicaRepository clinicaRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private MedicosRepository medicosRepository;



    @Override
    @Transactional
    public ClinicaResponseCreateDTO criarClinica(ClinicaCreateDTO dto) {
    
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String loginDoUsuarioLogado = authentication.getName();

    UserDetails userDetails = usuariosRepository.findByLogin(loginDoUsuarioLogado);

    if(userDetails == null){
        throw new RuntimeException("Usuário não encontrado: " + loginDoUsuarioLogado);
    }

    Usuario usuarioLogado = (Usuario) userDetails;

    if(usuarioLogado.getRole() != UserRole.CLINICA){
        throw new PermissaoException("Apenas usuários do tipo CLINICA podem cadastrar clínicas.");
    }

    boolean jaPossuiClinica = clinicaRepository.existsByUsuario(usuarioLogado);

    if(jaPossuiClinica){
        throw new ClinicaJaExisteException("O usuário logado já possui uma clínica cadastrada.");
    }
    
    Clinica clinicaSendoCadastrada = converteDtoParaEntidade(dto, usuarioLogado);

    if(verificarSeAClinicaJaExiste(clinicaSendoCadastrada)){
        throw new ClinicaJaExisteException("Já existe uma clínica com essa localização: contate o administrador do sistema");
    }

    Clinica clinicaSalva = clinicaRepository.save(clinicaSendoCadastrada);
    ClinicaResponseCreateDTO respostaCadastroDeClinica = new ClinicaResponseCreateDTO(clinicaSalva.getNome(), 
    "Clinica " + clinicaSalva.getNome() + " salva com sucesso!", clinicaSalva.getId());

    return respostaCadastroDeClinica;

    }

    @Override
    public List<ClinicaResponseReadDTO> listarClinicas(){
        if(verificarSeExistemClinicasCadastradas()){
        List<ClinicaResponseReadDTO> listaDeClinicas = montarListaDeClinicas();
        return listaDeClinicas;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public ClinicaResponseDeleteDTO deletarClinica(Long id){
        Clinica clinicaSendoDeletada = clinicaRepository.findById(id).orElseThrow(() -> new RuntimeException("Clínica não encontrada com o ID: " + id));
        clinicaRepository.delete(clinicaSendoDeletada);
        ClinicaResponseDeleteDTO respostaDeletarClinica = new ClinicaResponseDeleteDTO(clinicaSendoDeletada.getNome(), "Clínica " + clinicaSendoDeletada.getNome() + " deletada com sucesso!");
        return respostaDeletarClinica;
    }

    @Override
    public ClinicaResponseUpdateDTO atualizarClinica(Long id, ClinicaUpdateDTO dto){
        Clinica clinicaSendoAtualizada = clinicaRepository.findById(id).orElseThrow(() -> new RuntimeException("Clínica não encontrada com o ID: " + id));
        clinicaSendoAtualizada.setNome(dto.getNome());
        clinicaSendoAtualizada.setCnpj(dto.getCnpj());
        clinicaSendoAtualizada.setEmail(dto.getEmail());
        clinicaSendoAtualizada.setEndereco(dto.getEndereco());
        clinicaSendoAtualizada.setTelefone(dto.getTelefone());
        clinicaSendoAtualizada.setEspecializacoes(dto.getEspecializacoes());
        clinicaSendoAtualizada.setHorarioFuncionamentoFinal(dto.getHorarioFuncionamentoFinal());
        clinicaSendoAtualizada.setHorarioFuncionamentoInicio(dto.getHorarioFuncionamentoFinal());
        clinicaSendoAtualizada.setLatitude(dto.getLatitude());
        clinicaSendoAtualizada.setLongitude(dto.getLongitude());
        if(dto.getSite() != null){
            clinicaSendoAtualizada.setSite(dto.getSite());
            } else {
            clinicaSendoAtualizada.setSite(null);    
            }
        Clinica clinicaAtualizada = clinicaRepository.save(clinicaSendoAtualizada);
        ClinicaResponseUpdateDTO respostaAtualizarClinica = new ClinicaResponseUpdateDTO(clinicaAtualizada.getNome(), "Clínica " + clinicaAtualizada.getNome() + " atualizada com sucesso!");
        return respostaAtualizarClinica;
    }

    @Override
    public ClinicaResponseReadDTO pegarClinica(Long id){
        Clinica clinica = clinicaRepository.findById(id)
                .orElseThrow(() -> new ClinicaNaoExisteException("A clinica com id " + id + " não existe no banco de dados"));
        ClinicaResponseReadDTO clinicaRecuperadaConvertidaParaDTO = new ClinicaResponseReadDTO();
        clinicaRecuperadaConvertidaParaDTO.setId(clinica.getId());
        clinicaRecuperadaConvertidaParaDTO.setNome(clinica.getNome());
        clinicaRecuperadaConvertidaParaDTO.setCnpj(clinica.getCnpj());
        clinicaRecuperadaConvertidaParaDTO.setEmail(clinica.getEmail());
        clinicaRecuperadaConvertidaParaDTO.setTelefone(clinica.getTelefone());
        clinicaRecuperadaConvertidaParaDTO.setEndereco(clinica.getEndereco());
        clinicaRecuperadaConvertidaParaDTO.setSite(clinica.getSite());
        clinicaRecuperadaConvertidaParaDTO.setHorarioFuncionamentoInicio(clinica.getHorarioFuncionamentoInicio());
        clinicaRecuperadaConvertidaParaDTO.setHorarioFuncionamentoFinal(clinica.getHorarioFuncionamentoFinal());
        clinicaRecuperadaConvertidaParaDTO.setLatitude(clinica.getLatitude());
        clinicaRecuperadaConvertidaParaDTO.setLongitude(clinica.getLongitude());

        if (clinica.getEspecializacoes() != null) {
        clinicaRecuperadaConvertidaParaDTO.setEspecializacoes(
            clinica.getEspecializacoes().stream()
                   .map(Enum::name) 
                   .toList()        
        );
    }


        return clinicaRecuperadaConvertidaParaDTO;
    } 

    @Override
    public List<ClinicaResumoDTO> listarClinicasResumo() {
    List<Clinica> clinicas = clinicaRepository.findAll();
    if(clinicas == null){
        throw new EntityNotFoundException("Nenhuma clínica encontrada no banco de dados.");
    }
    List<ClinicaResumoDTO> clinicasResumo =  new ArrayList<>();
    clinicas.forEach(clinica ->  {
    clinicasResumo.add(new ClinicaResumoDTO(clinica.getId(), clinica.getNome(), clinica.getEndereco()));
    }
    );
    return clinicasResumo;
    
    }

    
    @Override
    public Set<String> listarEspecialidadesClinica(Long clinicaId) {

    List<Medico> medicos = medicosRepository.findByClinicaId(clinicaId);

    if(medicos == null){
        throw new EntityNotFoundException("Médicos não encontrados na busca de especializações.");
    }

    Set<String> especialidades = new HashSet<>();

    medicos.forEach(medico -> {
        especialidades.add(medico.getEspecializacao());
    });

    return especialidades;

    }




    public Clinica converteDtoParaEntidade(ClinicaCreateDTO dto, Usuario usuarioLogado){
        Clinica clinicaSendoCadastrada = new Clinica();
        clinicaSendoCadastrada.setNome(dto.getNome());
        clinicaSendoCadastrada.setCnpj(dto.getCnpj());
        clinicaSendoCadastrada.setEmail(dto.getEmail());
        clinicaSendoCadastrada.setEndereco(dto.getEndereco());
        clinicaSendoCadastrada.setTelefone(dto.getTelefone());
        clinicaSendoCadastrada.setEspecializacoes(dto.getEspecializacoes());
        clinicaSendoCadastrada.setHorarioFuncionamentoFinal(dto.getHorarioFuncionamentoFinal());
        clinicaSendoCadastrada.setHorarioFuncionamentoInicio(dto.getHorarioFuncionamentoInicio());
        clinicaSendoCadastrada.setLatitude(dto.getLatitude());
        clinicaSendoCadastrada.setLongitude(dto.getLongitude());
        if(dto.getSite() != null){
        clinicaSendoCadastrada.setSite(dto.getSite());
        } else {
        clinicaSendoCadastrada.setSite(null);    
        }
        clinicaSendoCadastrada.setUsuario(usuarioLogado);
        return clinicaSendoCadastrada;
    }

    public boolean verificarSeAClinicaJaExiste(Clinica clinica){
       Clinica clinicaSendoVerificada = clinicaRepository.findByLatitudeAndLongitude(clinica.getLatitude(),clinica.getLongitude());
       if(clinicaSendoVerificada != null){
        return true;
       } else {
        return false;
       }
    }

    public boolean verificarSeExistemClinicasCadastradas(){
        if(clinicaRepository.findAll() != null){
            return true;
        } else {
            return false;
        }
    }

    public List<ClinicaResponseReadDTO> montarListaDeClinicas(){
        List<Clinica> clinicas = clinicaRepository.findAll();
        List<ClinicaResponseReadDTO> clinicasResponse = new ArrayList<>();
        clinicas.forEach(clinica -> {
            ClinicaResponseReadDTO clinicaResponse = new ClinicaResponseReadDTO();
            clinicaResponse.setId(clinica.getId());
            clinicaResponse.setNome(clinica.getNome());
            clinicaResponse.setCnpj(clinica.getCnpj());
            clinicaResponse.setEmail(clinica.getEmail());
            clinicaResponse.setTelefone(clinica.getTelefone());
            clinicaResponse.setEndereco(clinica.getEndereco());
            clinicaResponse.setSite(clinica.getSite());
            clinicaResponse.setHorarioFuncionamentoInicio(clinica.getHorarioFuncionamentoInicio());
            clinicaResponse.setHorarioFuncionamentoFinal(clinica.getHorarioFuncionamentoFinal());
            clinicaResponse.setLatitude(clinica.getLatitude());
            clinicaResponse.setLongitude(clinica.getLongitude());
            clinicasResponse.add(clinicaResponse);
        });
        return clinicasResponse;
    }



    
}
