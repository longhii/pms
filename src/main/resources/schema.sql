ALTER TABLE IF EXISTS public.psicologos ALTER COLUMN id SET DEFAULT nextval('psicologos_id_seq');
ALTER TABLE IF EXISTS public.consultas ALTER COLUMN id SET DEFAULT nextval('consultas_id_seq');
ALTER TABLE IF EXISTS public.pacientes ALTER COLUMN id SET DEFAULT nextval('pacientes_id_seq');
ALTER TABLE IF EXISTS public.pagamentos ALTER COLUMN id SET DEFAULT nextval('pagamentos_id_seq');