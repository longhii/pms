insert into psicologos (login, senha, nome) values
('admin', '$2a$10$jpLNVNeA7Ar/ZQ2DKbKCm.MuT2ESe.Qop96jipKMq7RaUgCoQedV.' , 'Psicólogo Admin'),
('user', '$2a$10$jpLNVNeA7Ar/ZQ2DKbKCm.MuT2ESe.Qop96jipKMq7RaUgCoQedV.' , 'Psicólogo User');

INSERT INTO pacientes(created_at, id, psicologo_id, email, nome, telefone) VALUES
('2024-08-17', 1, 1, null, 'John Doe', '(41) 99900-0001'),
('2024-08-17', 2, 1, null, 'Jane Doe', '(41) 99900-0002'),
('2024-08-17', 3, 1, null, 'Richard Roe', '(41) 99900-0003'),
('2024-08-17', 4, 1, null, 'John Smith', '(41) 99900-0004');

INSERT INTO consultas (data, hora_inicio, hora_fim,status, paciente_id) VALUES
('2024-08-01', '14:00:00', '15:00:00', 'ATENDIDO', 2),
('2024-08-01', '17:30:00', '18:30:00', 'ATENDIDO', 3),
('2024-08-01', '18:30:00', '19:30:00', 'ATENDIDO', 1),
('2024-08-02', '17:30:00', '18:30:00', 'ATENDIDO', 1),
('2024-08-09', '17:30:00', '18:30:00', 'ATENDIDO', 1),
('2024-08-15', '18:30:00', '19:30:00', 'CANCELADO', 1),
('2024-08-16', '13:30:00', '14:30:00', 'ATENDIDO', 1),
('2024-08-16', '15:00:00', '16:00:00', 'ATENDIDO', 2),
('2024-08-16', '16:30:00', '17:30:00', 'ATENDIDO', 3),
('2024-08-22', '14:30:00', '15:30:00', 'EM_ESPERA', 2),
('2024-08-23', '16:30:00', '17:30:00', 'EM_ESPERA', 3);