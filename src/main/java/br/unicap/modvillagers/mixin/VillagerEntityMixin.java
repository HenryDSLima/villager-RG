package br.unicap.modvillagers.mixin;

import br.unicap.modvillagers.VillagerRGMod;
import br.unicap.modvillagers.eventos.RegistroCivilEventBus;
import br.unicap.modvillagers.excecoes.PersistenciaNBTException;
import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.modelo.VillagerDocumentHolder;
import br.unicap.modvillagers.persistencia.VillagerNbtSerializer;
import br.unicap.modvillagers.servico.VillagerTradutor;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.village.VillagerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin aplicado à entidade nativa {@link VillagerEntity} do Minecraft para:
 * <ul>
 *   <li>Anexar por composição um {@link VillagerDocument} persistente;</li>
 *   <li>Gravar e restaurar os dados civis no NBT nativo do mundo ({@code writeCustomDataToNbt} / {@code readCustomDataFromNbt});</li>
 *   <li>Atualizar automaticamente o histórico de profissões quando {@code setVillagerData} altera a profissão do Villager.</li>
 * </ul>
 */
@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin implements VillagerDocumentHolder {

    @Unique
    private VillagerDocument villagerrg$documento;

    @Override
    public VillagerDocument villagerrg$getDocumento() {
        return this.villagerrg$documento;
    }

    @Override
    public void villagerrg$setDocumento(VillagerDocument documento) {
        this.villagerrg$documento = documento;
    }

    @Override
    public VillagerDocument villagerrg$garantirDocumento() {
        VillagerEntity self = (VillagerEntity) (Object) this;
        if (this.villagerrg$documento == null) {
            String bioma = VillagerTradutor.traduzirBioma(self.getVillagerData().getType());
            String profissao = VillagerTradutor.traduzirProfissao(self.getVillagerData().getProfession());
            this.villagerrg$documento = RegistroCivilEventBus.getInstance()
                    .emitirDocumentoParaVillager(self.getUuid(), bioma, profissao);
        }
        return this.villagerrg$documento;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void villagerrg$salvarDocumentoNoNbt(NbtCompound nbt, CallbackInfo ci) {
        VillagerDocument doc = this.villagerrg$documento;
        if (doc != null) {
            try {
                VillagerNbtSerializer.writeToNbt(doc, nbt);
            } catch (PersistenciaNBTException e) {
                VillagerRGMod.LOGGER.error("Erro ao gravar documento civil no NBT do Villager", e);
            }
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void villagerrg$carregarDocumentoDoNbt(NbtCompound nbt, CallbackInfo ci) {
        try {
            VillagerDocument restaurado = VillagerNbtSerializer.readFromNbt(nbt);
            if (restaurado != null) {
                this.villagerrg$documento = restaurado;
            }
        } catch (PersistenciaNBTException e) {
            VillagerRGMod.LOGGER.warn("Tag NBT VillagerRG corrompida; novo documento será emitido no carregamento.", e);
            this.villagerrg$documento = null;
        }
    }

    @Inject(method = "setVillagerData", at = @At("TAIL"))
    private void villagerrg$registrarMudancaProfissao(VillagerData villagerData, CallbackInfo ci) {
        VillagerEntity self = (VillagerEntity) (Object) this;
        if (self.getWorld().isClient() || this.villagerrg$documento == null) {
            return;
        }

        String novaProfissao = VillagerTradutor.traduzirProfissao(villagerData.getProfession());
        int tamanhoAntes = this.villagerrg$documento.getHistoricoProfissoes().size();
        this.villagerrg$documento.adicionarProfissao(novaProfissao);

        if (this.villagerrg$documento.getHistoricoProfissoes().size() > tamanhoAntes) {
            RegistroCivilEventBus.getInstance()
                    .onProfissaoAtualizada(self.getUuid(), this.villagerrg$documento, novaProfissao);
        }
    }
}
