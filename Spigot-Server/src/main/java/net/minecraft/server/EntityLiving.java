package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

// CraftBukkit start
import java.util.ArrayList;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
// CraftBukkit end

import org.bukkit.craftbukkit.SpigotTimings; // Spigot

public abstract class EntityLiving extends Entity {

    private static final UUID a = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final AttributeModifier b = (new AttributeModifier(EntityLiving.a, "Sprinting speed boost", 0.30000001192092896D, 2)).a(false);
    private AttributeMapBase c;
    public CombatTracker combatTracker = new CombatTracker(this);
    public final Map effects = Maps.newHashMap();
    private final ItemStack[] h = new ItemStack[5];
    public boolean ap;
    public int aq;
    public int ar;
    public int hurtTicks;
    public int at;
    public float au;
    public int deathTicks;
    public float aw;
    public float ax;
    public float ay;
    public float az;
    public float aA;
    public int maxNoDamageTicks = 20;
    public float aC;
    public float aD;
    public float aE;
    public float aF;
    public float aG;
    public float aH;
    public float aI;
    public float aJ;
    public float aK = 0.02F;
    public EntityHuman killer;
    protected int lastDamageByPlayerTime;
    protected boolean aN;
    protected int aO;
    protected float aP;
    protected float aQ;
    protected float aR;
    protected float aS;
    protected float aT;
    protected int aU;
    public float lastDamage;
    protected boolean aW;
    public float aX;
    public float aY;
    protected float aZ;
    protected int ba;
    protected double bb;
    protected double bc;
    protected double bd;
    protected double be;
    protected double bf;
    public boolean updateEffects = true;
    public EntityLiving lastDamager;
    private int hurtTimestamp;
    private EntityLiving bi;
    private int bj;
    private float bk;
    private int bl;
    private float bm;
    // CraftBukkit start
    public int expToDrop;
    public int maxAirTicks = 300;
    ArrayList<org.bukkit.inventory.ItemStack> drops = null;
    // CraftBukkit end
    // Spigot start
    public void inactiveTick()
    {
        super.inactiveTick();
        ++this.aO; // Above all the floats
    }
    // Spigot end

    public void G() {
        this.damageEntity(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
    }

    public EntityLiving(World world) {
        super(world);
        this.aW();
        // CraftBukkit - setHealth(getMaxHealth()) inlined and simplified to skip the instanceof check for EntityPlayer, as getBukkitEntity() is not initialized in constructor
        this.datawatcher.watch(6, (float) this.getAttributeInstance(GenericAttributes.maxHealth).getValue());
        this.k = true;
        this.aF = (float) ((Math.random() + 1.0D) * 0.009999999776482582D);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.aE = (float) Math.random() * 12398.0F;
        this.yaw = (float) (Math.random() * 3.1415927410125732D * 2.0D);
        this.aI = this.yaw;
        this.S = 0.6F;
    }

    protected void h() {
        this.datawatcher.a(7, Integer.valueOf(0));
        this.datawatcher.a(8, Byte.valueOf((byte) 0));
        this.datawatcher.a(9, Byte.valueOf((byte) 0));
        this.datawatcher.a(6, Float.valueOf(1.0F));
    }

    protected void aW() {
        this.getAttributeMap().b(GenericAttributes.maxHealth);
        this.getAttributeMap().b(GenericAttributes.c);
        this.getAttributeMap().b(GenericAttributes.d);
    }

    protected void a(double d0, boolean flag, Block block, BlockPosition blockposition) {
        if (!this.V()) {
            this.W();
        }

        if (!this.world.isStatic && this.fallDistance > 3.0F && flag) {
            IBlockData iblockdata = this.world.getType(blockposition);
            Block block1 = iblockdata.getBlock();
            float f = (float) MathHelper.f(this.fallDistance - 3.0F);

            if (block1.getMaterial() != Material.AIR) {
                double d1 = (double) Math.min(0.2F + f / 15.0F, 10.0F);

                if (d1 > 2.5D) {
                    d1 = 2.5D;
                }

                int i = (int) (150.0D * d1);
                
                // CraftBukkit start - visiblity api
                if (this instanceof EntityPlayer) {
                    ((WorldServer) this.world).sendParticles((EntityPlayer) this, EnumParticle.BLOCK_DUST, false, this.locX, this.locY, this.locZ, i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, new int[] { Block.getCombinedId(iblockdata)});
                } else {
                    ((WorldServer) this.world).a(EnumParticle.BLOCK_DUST, this.locX, this.locY, this.locZ, i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, new int[] { Block.getCombinedId(iblockdata)}); 
                }
                // CraftBukkit end
            }
        }

        super.a(d0, flag, block, blockposition);
    }

    public boolean aX() {
        return false;
    }

    public void K() {
        this.aw = this.ax;
        super.K();
        this.world.methodProfiler.a("livingEntityBaseTick");
        boolean flag = this instanceof EntityHuman;

        if (this.isAlive()) {
            if (this.inBlock()) {
                this.damageEntity(DamageSource.STUCK, 1.0F);
            } else if (flag && !this.world.af().a(this.getBoundingBox())) {
                double d0 = this.world.af().a((Entity) this) + this.world.af().m();

                if (d0 < 0.0D) {
                    this.damageEntity(DamageSource.STUCK, (float) Math.max(1, MathHelper.floor(-d0 * this.world.af().n())));
                }
            }
        }

        if (this.isFireProof() || this.world.isStatic) {
            this.extinguish();
        }

        boolean flag1 = flag && ((EntityHuman) this).abilities.isInvulnerable;

        if (this.isAlive() && this.a(Material.WATER)) {
            if (!this.aX() && !this.hasEffect(MobEffectList.WATER_BREATHING.id) && !flag1) {
                this.setAirTicks(this.j(this.getAirTicks()));
                if (this.getAirTicks() == -20) {
                    this.setAirTicks(0);

                    for (int i = 0; i < 8; ++i) {
                        float f = this.random.nextFloat() - this.random.nextFloat();
                        float f1 = this.random.nextFloat() - this.random.nextFloat();
                        float f2 = this.random.nextFloat() - this.random.nextFloat();

                        this.world.addParticle(EnumParticle.WATER_BUBBLE, this.locX + (double) f, this.locY + (double) f1, this.locZ + (double) f2, this.motX, this.motY, this.motZ, new int[0]);
                    }

                    this.damageEntity(DamageSource.DROWN, 2.0F);
                }
            }

            if (!this.world.isStatic && this.av() && this.vehicle instanceof EntityLiving) {
                this.mount((Entity) null);
            }
        } else {
            // CraftBukkit start - Only set if needed to work around a DataWatcher inefficiency
            if (this.getAirTicks() != 300) {
                this.setAirTicks(maxAirTicks);
            }
            // CraftBukkit end
        }

        if (this.isAlive() && this.U()) {
            this.extinguish();
        }

        this.aC = this.aD;
        if (this.hurtTicks > 0) {
            --this.hurtTicks;
        }

        if (this.noDamageTicks > 0 && !(this instanceof EntityPlayer)) {
            --this.noDamageTicks;
        }

        if (this.getHealth() <= 0.0F) {
            this.aY();
        }

        if (this.lastDamageByPlayerTime > 0) {
            --this.lastDamageByPlayerTime;
        } else {
            this.killer = null;
        }

        if (this.bi != null && !this.bi.isAlive()) {
            this.bi = null;
        }

        if (this.lastDamager != null) {
            if (!this.lastDamager.isAlive()) {
                this.b((EntityLiving) null);
            } else if (this.ticksLived - this.hurtTimestamp > 100) {
                this.b((EntityLiving) null);
            }
        }

        this.bh();
        this.aS = this.aR;
        this.aH = this.aG;
        this.aJ = this.aI;
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        this.world.methodProfiler.b();
    }
    
    // CraftBukkit start
    public int getExpReward() {
        int exp = this.getExpValue(this.killer);

        if (!this.world.isStatic && (this.lastDamageByPlayerTime > 0 || this.alwaysGivesExp()) && this.aZ() && this.world.getGameRules().getBoolean("doMobLoot")) {
            return exp;
        } else {
            return 0;
        }
    }
    // CraftBukkit end    

    public boolean isBaby() {
        return false;
    }

    protected void aY() {
        ++this.deathTicks;
        if (this.deathTicks >= 20 && !this.dead) { // CraftBukkit - (this.deathTicks == 20) -> (this.deathTicks >= 20 && !this.dead)
            int i;

            // CraftBukkit start - Update getExpReward() above if the removed if() changes!
            i = this.expToDrop;
            while (i > 0) {
                int j = EntityExperienceOrb.getOrbValue(i);
                i -= j;
                this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY, this.locZ, j));
            }
            this.expToDrop = 0;
            // CraftBukkit end

            this.die();

            for (i = 0; i < 20; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;

                this.world.addParticle(EnumParticle.EXPLOSION_NORMAL, this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, this.locY + (double) (this.random.nextFloat() * this.length), this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2, new int[0]);
            }
        }

    }

    protected boolean aZ() {
        return !this.isBaby();
    }

    protected int j(int i) {
        int j = EnchantmentManager.getOxygenEnchantmentLevel(this);

        return j > 0 && this.random.nextInt(j + 1) > 0 ? i : i - 1;
    }

    protected int getExpValue(EntityHuman entityhuman) {
        return 0;
    }

    protected boolean alwaysGivesExp() {
        return false;
    }

    public Random bb() {
        return this.random;
    }

    public EntityLiving getLastDamager() {
        return this.lastDamager;
    }

    public int bd() {
        return this.hurtTimestamp;
    }

    public void b(EntityLiving entityliving) {
        this.lastDamager = entityliving;
        this.hurtTimestamp = this.ticksLived;
    }

    public EntityLiving be() {
        return this.bi;
    }

    public int bf() {
        return this.bj;
    }

    public void p(Entity entity) {
        if (entity instanceof EntityLiving) {
            this.bi = (EntityLiving) entity;
        } else {
            this.bi = null;
        }

        this.bj = this.ticksLived;
    }

    public int bg() {
        return this.aO;
    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setFloat("HealF", this.getHealth());
        nbttagcompound.setShort("Health", (short) ((int) Math.ceil((double) this.getHealth())));
        nbttagcompound.setShort("HurtTime", (short) this.hurtTicks);
        nbttagcompound.setInt("HurtByTimestamp", this.hurtTimestamp);
        nbttagcompound.setShort("DeathTime", (short) this.deathTicks);
        nbttagcompound.setFloat("AbsorptionAmount", this.getAbsorptionHearts());
        ItemStack[] aitemstack = this.getEquipment();
        int i = aitemstack.length;

        int j;
        ItemStack itemstack;

        for (j = 0; j < i; ++j) {
            itemstack = aitemstack[j];
            if (itemstack != null) {
                this.c.a(itemstack.B());
            }
        }

        nbttagcompound.set("Attributes", GenericAttributes.a(this.getAttributeMap()));
        aitemstack = this.getEquipment();
        i = aitemstack.length;

        for (j = 0; j < i; ++j) {
            itemstack = aitemstack[j];
            if (itemstack != null) {
                this.c.b(itemstack.B());
            }
        }

        if (!this.effects.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.effects.values().iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                nbttaglist.add(mobeffect.a(new NBTTagCompound()));
            }

            nbttagcompound.set("ActiveEffects", nbttaglist);
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        this.setAbsorptionHearts(nbttagcompound.getFloat("AbsorptionAmount"));
        if (nbttagcompound.hasKeyOfType("Attributes", 9) && this.world != null && !this.world.isStatic) {
            GenericAttributes.a(this.getAttributeMap(), nbttagcompound.getList("Attributes", 10));
        }

        if (nbttagcompound.hasKeyOfType("ActiveEffects", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("ActiveEffects", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.get(i);
                MobEffect mobeffect = MobEffect.b(nbttagcompound1);

                if (mobeffect != null) {
                    this.effects.put(Integer.valueOf(mobeffect.getEffectId()), mobeffect);
                }
            }
        }
        
        // CraftBukkit start
        if (nbttagcompound.hasKey("Bukkit.MaxHealth")) {
            NBTBase nbtbase = nbttagcompound.get("Bukkit.MaxHealth");
            if (nbtbase.getTypeId() == 5) {
                this.getAttributeInstance(GenericAttributes.maxHealth).setValue((double) ((NBTTagFloat) nbtbase).c());
            } else if (nbtbase.getTypeId() == 3) {
                this.getAttributeInstance(GenericAttributes.maxHealth).setValue((double) ((NBTTagInt) nbtbase).d());
            }
        }
        // CraftBukkit end

        if (nbttagcompound.hasKeyOfType("HealF", 99)) {
            this.setHealth(nbttagcompound.getFloat("HealF"));
        } else {
            NBTBase nbtbase = nbttagcompound.get("Health");

            if (nbtbase == null) {
                this.setHealth(this.getMaxHealth());
            } else if (nbtbase.getTypeId() == 5) {
                this.setHealth(((NBTTagFloat) nbtbase).h());
            } else if (nbtbase.getTypeId() == 2) {
                this.setHealth((float) ((NBTTagShort) nbtbase).e());
            }
        }

        this.hurtTicks = nbttagcompound.getShort("HurtTime");
        this.deathTicks = nbttagcompound.getShort("DeathTime");
        this.hurtTimestamp = nbttagcompound.getInt("HurtByTimestamp");
    }

    // CraftBukkit start
    private boolean isTickingEffects = false;
    private List<Object> effectsToProcess = Lists.newArrayList();
    // CraftBukkit end

    protected void bh() {
        Iterator iterator = this.effects.keySet().iterator();
        isTickingEffects = true; // CraftBukkit
        while (iterator.hasNext()) {
            Integer integer = (Integer) iterator.next();
            MobEffect mobeffect = (MobEffect) this.effects.get(integer);

            if (!mobeffect.tick(this)) {
                if (!this.world.isStatic) {
                    iterator.remove();
                    this.b(mobeffect);
                }
            } else if (mobeffect.getDuration() % 600 == 0) {
                this.a(mobeffect, false);
            }
        }
        // CraftBukkit start
        isTickingEffects = false;
        for (Object e : effectsToProcess) {
            if (e instanceof MobEffect) {
                addEffect((MobEffect) e);
            } else {
                removeEffect((Integer) e);
            }
        }
        // CraftBukkit end

        if (this.updateEffects) {
            if (!this.world.isStatic) {
                this.B();
            }

            this.updateEffects = false;
        }

        int i = this.datawatcher.getInt(7);
        boolean flag = this.datawatcher.getByte(8) > 0;

        if (i > 0) {
            boolean flag1 = false;

            if (!this.isInvisible()) {
                flag1 = this.random.nextBoolean();
            } else {
                flag1 = this.random.nextInt(15) == 0;
            }

            if (flag) {
                flag1 &= this.random.nextInt(5) == 0;
            }

            if (flag1 && i > 0) {
                double d0 = (double) (i >> 16 & 255) / 255.0D;
                double d1 = (double) (i >> 8 & 255) / 255.0D;
                double d2 = (double) (i >> 0 & 255) / 255.0D;

                this.world.addParticle(flag ? EnumParticle.SPELL_MOB_AMBIENT : EnumParticle.SPELL_MOB, this.locX + (this.random.nextDouble() - 0.5D) * (double) this.width, this.locY + this.random.nextDouble() * (double) this.length, this.locZ + (this.random.nextDouble() - 0.5D) * (double) this.width, d0, d1, d2, new int[0]);
            }
        }

    }

    protected void B() {
        if (this.effects.isEmpty()) {
            this.bi();
            this.setInvisible(false);
        } else {
            int i = PotionBrewer.a(this.effects.values());

            this.datawatcher.watch(8, Byte.valueOf((byte) (PotionBrewer.b(this.effects.values()) ? 1 : 0)));
            this.datawatcher.watch(7, Integer.valueOf(i));
            this.setInvisible(this.hasEffect(MobEffectList.INVISIBILITY.id));
        }

    }

    protected void bi() {
        this.datawatcher.watch(8, Byte.valueOf((byte) 0));
        this.datawatcher.watch(7, Integer.valueOf(0));
    }

    public void removeAllEffects() {
        Iterator iterator = this.effects.keySet().iterator();

        while (iterator.hasNext()) {
            Integer integer = (Integer) iterator.next();
            MobEffect mobeffect = (MobEffect) this.effects.get(integer);

            if (!this.world.isStatic) {
                iterator.remove();
                this.b(mobeffect);
            }
        }

    }

    public Collection getEffects() {
        return this.effects.values();
    }

    public boolean hasEffect(int i) {
        // CraftBukkit - Add size check for efficiency
        return this.effects.size() != 0 && this.effects.containsKey(Integer.valueOf(i));
    }

    public boolean hasEffect(MobEffectList mobeffectlist) {
        return this.effects.containsKey(Integer.valueOf(mobeffectlist.id));
    }

    public MobEffect getEffect(MobEffectList mobeffectlist) {
        return (MobEffect) this.effects.get(Integer.valueOf(mobeffectlist.id));
    }

    public void addEffect(MobEffect mobeffect) {
        org.spigotmc.AsyncCatcher.catchOp( "effect add"); // Spigot
        // CraftBukkit start
        if (isTickingEffects) {
            effectsToProcess.add(mobeffect);
            return;
        }
        // CraftBukkit end
        if (this.d(mobeffect)) {
            if (this.effects.containsKey(Integer.valueOf(mobeffect.getEffectId()))) {
                ((MobEffect) this.effects.get(Integer.valueOf(mobeffect.getEffectId()))).a(mobeffect);
                this.a((MobEffect) this.effects.get(Integer.valueOf(mobeffect.getEffectId())), true);
            } else {
                this.effects.put(Integer.valueOf(mobeffect.getEffectId()), mobeffect);
                this.a(mobeffect);
            }

        }
    }

    public boolean d(MobEffect mobeffect) {
        if (this.getMonsterType() == EnumMonsterType.UNDEAD) {
            int i = mobeffect.getEffectId();

            if (i == MobEffectList.REGENERATION.id || i == MobEffectList.POISON.id) {
                return false;
            }
        }

        return true;
    }

    public boolean bl() {
        return this.getMonsterType() == EnumMonsterType.UNDEAD;
    }

    public void removeEffect(int i) {
        // CraftBukkit start
        if (isTickingEffects) {
            effectsToProcess.add(i);
            return;
        }
        // CraftBukkit end
        MobEffect mobeffect = (MobEffect) this.effects.remove(Integer.valueOf(i));

        if (mobeffect != null) {
            this.b(mobeffect);
        }

    }

    protected void a(MobEffect mobeffect) {
        this.updateEffects = true;
        if (!this.world.isStatic) {
            MobEffectList.byId[mobeffect.getEffectId()].b(this, this.getAttributeMap(), mobeffect.getAmplifier());
        }

    }

    protected void a(MobEffect mobeffect, boolean flag) {
        this.updateEffects = true;
        if (flag && !this.world.isStatic) {
            MobEffectList.byId[mobeffect.getEffectId()].a(this, this.getAttributeMap(), mobeffect.getAmplifier());
            MobEffectList.byId[mobeffect.getEffectId()].b(this, this.getAttributeMap(), mobeffect.getAmplifier());
        }

    }

    protected void b(MobEffect mobeffect) {
        this.updateEffects = true;
        if (!this.world.isStatic) {
            MobEffectList.byId[mobeffect.getEffectId()].a(this, this.getAttributeMap(), mobeffect.getAmplifier());
        }

    }

    // CraftBukkit start - Delegate so we can handle providing a reason for health being regained
    public void heal(float f) {
        heal(f, EntityRegainHealthEvent.RegainReason.CUSTOM);
    }
        
    public void heal(float f, EntityRegainHealthEvent.RegainReason regainReason) {
        float f1 = this.getHealth();

        if (f1 > 0.0F) {
            EntityRegainHealthEvent event = new EntityRegainHealthEvent(this.getBukkitEntity(), f, regainReason);
            this.world.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                this.setHealth((float) (this.getHealth() + event.getAmount()));
            }
            // CraftBukkit end
        }

    }

    public final float getHealth() {
        // CraftBukkit start - Use unscaled health
        if (this instanceof EntityPlayer) {
            return (float) ((EntityPlayer) this).getBukkitEntity().getHealth();
        }
        // CraftBukkit end
        return this.datawatcher.getFloat(6);
    }

    public void setHealth(float f) {
        // CraftBukkit start - Handle scaled health
        if (this instanceof EntityPlayer) {
            org.bukkit.craftbukkit.entity.CraftPlayer player = ((EntityPlayer) this).getBukkitEntity();
            // Squeeze
            if (f < 0.0F) {
                player.setRealHealth(0.0D);
            } else if (f > player.getMaxHealth()) {
                player.setRealHealth(player.getMaxHealth());
            } else {
                player.setRealHealth(f);
            }

            this.datawatcher.watch(6, Float.valueOf(player.getScaledHealth()));
            return;
        }
        // CraftBukkit end
        this.datawatcher.watch(6, Float.valueOf(MathHelper.a(f, 0.0F, this.getMaxHealth())));
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (this.world.isStatic) {
            return false;
        } else {
            this.aO = 0;
            if (this.getHealth() <= 0.0F) {
                return false;
            } else if (damagesource.o() && this.hasEffect(MobEffectList.FIRE_RESISTANCE)) {
                return false;
            } else {
                // CraftBukkit - Moved into d(DamageSource, float)
                if (false && (damagesource == DamageSource.ANVIL || damagesource == DamageSource.FALLING_BLOCK) && this.getEquipment(4) != null) {
                    this.getEquipment(4).damage((int) (f * 4.0F + this.random.nextFloat() * f * 2.0F), this);
                    f *= 0.75F;
                }

                this.az = 1.5F;
                boolean flag = true;

                if ((float) this.noDamageTicks > (float) this.maxNoDamageTicks / 2.0F) {
                    if (f <= this.lastDamage) {
                        return false;
                    }

                    // CraftBukkit start
                    if (!this.d(damagesource, f - this.lastDamage)) {
                        return false;
                    }
                    // CraftBukkit end
                    this.lastDamage = f;
                    flag = false;
                } else {
                    // CraftBukkit start
                    float previousHealth = this.getHealth();
                    if (!this.d(damagesource, f)) {
                        return false;
                    }
                    this.lastDamage = f;
                    this.noDamageTicks = this.maxNoDamageTicks;
                    // CraftBukkit end
                    this.hurtTicks = this.at = 10;
                }

                // CraftBukkit start
                if(this instanceof EntityAnimal){
                    ((EntityAnimal)this).cq();
                    if(this instanceof EntityTameableAnimal){
                        ((EntityTameableAnimal)this).getGoalSit().setSitting(false);
                    }
                }
                // CraftBukkit end

                this.au = 0.0F;
                Entity entity = damagesource.getEntity();

                if (entity != null) {
                    if (entity instanceof EntityLiving) {
                        this.b((EntityLiving) entity);
                    }

                    if (entity instanceof EntityHuman) {
                        this.lastDamageByPlayerTime = 100;
                        this.killer = (EntityHuman) entity;
                    } else if (entity instanceof EntityWolf) {
                        EntityWolf entitywolf = (EntityWolf) entity;

                        if (entitywolf.isTamed()) {
                            this.lastDamageByPlayerTime = 100;
                            this.killer = null;
                        }
                    }
                }

                if (flag) {
                    this.world.broadcastEntityEffect(this, (byte) 2);
                    if (damagesource != DamageSource.DROWN) {
                        this.ac();
                    }

                    if (entity != null) {
                        double d0 = entity.locX - this.locX;

                        double d1;

                        for (d1 = entity.locZ - this.locZ; d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                            d0 = (Math.random() - Math.random()) * 0.01D;
                        }

                        this.au = (float) (Math.atan2(d1, d0) * 180.0D / 3.1415927410125732D - (double) this.yaw);
                        this.a(entity, f, d0, d1);
                    } else {
                        this.au = (float) ((int) (Math.random() * 2.0D) * 180);
                    }
                }

                String s;

                if (this.getHealth() <= 0.0F) {
                    s = this.bo();
                    if (flag && s != null) {
                        this.makeSound(s, this.bA(), this.bB());
                    }

                    this.die(damagesource);
                } else {
                    s = this.bn();
                    if (flag && s != null) {
                        this.makeSound(s, this.bA(), this.bB());
                    }
                }

                return true;
            }
        }
    }

    public void b(ItemStack itemstack) {
        this.makeSound("random.break", 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);

        for (int i = 0; i < 5; ++i) {
            Vec3D vec3d = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);

            vec3d = vec3d.a(-this.pitch * 3.1415927F / 180.0F);
            vec3d = vec3d.b(-this.yaw * 3.1415927F / 180.0F);
            double d0 = (double) (-this.random.nextFloat()) * 0.6D - 0.3D;
            Vec3D vec3d1 = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);

            vec3d1 = vec3d1.a(-this.pitch * 3.1415927F / 180.0F);
            vec3d1 = vec3d1.b(-this.yaw * 3.1415927F / 180.0F);
            vec3d1 = vec3d1.add(this.locX, this.locY + (double) this.getHeadHeight(), this.locZ);
            this.world.addParticle(EnumParticle.ITEM_CRACK, vec3d1.a, vec3d1.b, vec3d1.c, vec3d.a, vec3d.b + 0.05D, vec3d.c, new int[] { Item.getId(itemstack.getItem())});
        }

    }

    public void die(DamageSource damagesource) {
        Entity entity = damagesource.getEntity();
        EntityLiving entityliving = this.bs();

        if (this.aU >= 0 && entityliving != null) {
            entityliving.b(this, this.aU);
        }

        if (entity != null) {
            entity.a(this);
        }

        this.aN = true;
        this.br().g();
        if (!this.world.isStatic) {
            int i = 0;

            if (entity instanceof EntityHuman) {
                i = EnchantmentManager.getBonusMonsterLootEnchantmentLevel((EntityLiving) entity);
            }

            if (this.aZ() && this.world.getGameRules().getBoolean("doMobLoot")) {
                this.drops = new ArrayList<org.bukkit.inventory.ItemStack>(); // CraftBukkit - Setup drop capture
                 
                this.dropDeathLoot(this.lastDamageByPlayerTime > 0, i);
                this.dropEquipment(this.lastDamageByPlayerTime > 0, i);
                if (this.lastDamageByPlayerTime > 0 && this.random.nextFloat() < 0.025F + (float) i * 0.01F) {
                    this.getRareDrop();
                } 
                // CraftBukkit start - Call death event
                CraftEventFactory.callEntityDeathEvent(this, this.drops);
                this.drops = null;
            } else {
                CraftEventFactory.callEntityDeathEvent(this);
                // CraftBukkit end
            }
        }

        this.world.broadcastEntityEffect(this, (byte) 3);
    }

    protected void dropEquipment(boolean flag, int i) {}

    public void a(Entity entity, float f, double d0, double d1) {
        if (this.random.nextDouble() >= this.getAttributeInstance(GenericAttributes.c).getValue()) {
            this.ai = true;
            float f1 = MathHelper.sqrt(d0 * d0 + d1 * d1);
            float f2 = 0.4F;

            this.motX /= 2.0D;
            this.motY /= 2.0D;
            this.motZ /= 2.0D;
            this.motX -= d0 / (double) f1 * (double) f2;
            this.motY += (double) f2;
            this.motZ -= d1 / (double) f1 * (double) f2;
            if (this.motY > 0.4000000059604645D) {
                this.motY = 0.4000000059604645D;
            }

        }
    }

    protected String bn() {
        return "game.neutral.hurt";
    }

    protected String bo() {
        return "game.neutral.die";
    }

    protected void getRareDrop() {}

    protected void dropDeathLoot(boolean flag, int i) {}

    public boolean j_() {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.getBoundingBox().b);
        int k = MathHelper.floor(this.locZ);
        Block block = this.world.getType(new BlockPosition(i, j, k)).getBlock();

        return (block == Blocks.LADDER || block == Blocks.VINE) && (!(this instanceof EntityHuman) || !((EntityHuman) this).v());
    }

    public boolean isAlive() {
        return !this.dead && this.getHealth() > 0.0F;
    }

    public void e(float f, float f1) {
        super.e(f, f1);
        MobEffect mobeffect = this.getEffect(MobEffectList.JUMP);
        float f2 = mobeffect != null ? (float) (mobeffect.getAmplifier() + 1) : 0.0F;
        int i = MathHelper.f((f - 3.0F - f2) * f1);

        if (i > 0) {
            // CraftBukkit start
            if (!this.damageEntity(DamageSource.FALL, (float) i)) {
                return;
            }
            // CraftBukkit end
            this.makeSound(this.n(i), 1.0F, 1.0F);
            // this.damageEntity(DamageSource.FALL, (float) i); // CraftBukkit - moved up
            int j = MathHelper.floor(this.locX);
            int k = MathHelper.floor(this.locY - 0.20000000298023224D);
            int l = MathHelper.floor(this.locZ);
            Block block = this.world.getType(new BlockPosition(j, k, l)).getBlock();

            if (block.getMaterial() != Material.AIR) {
                StepSound stepsound = block.stepSound;

                this.makeSound(stepsound.getStepSound(), stepsound.getVolume1() * 0.5F, stepsound.getVolume2() * 0.75F);
            }
        }

    }

    protected String n(int i) {
        return i > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
    }

    public int bq() {
        int i = 0;
        ItemStack[] aitemstack = this.getEquipment();
        int j = aitemstack.length;

        for (int k = 0; k < j; ++k) {
            ItemStack itemstack = aitemstack[k];

            if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
                int l = ((ItemArmor) itemstack.getItem()).c;

                i += l;
            }
        }

        return i;
    }

    protected void damageArmor(float f) {}

    protected float applyArmorModifier(DamageSource damagesource, float f) {
        if (!damagesource.ignoresArmor()) {
            int i = 25 - this.bq();
            float f1 = f * (float) i;

            // this.damageArmor(f); // CraftBukkit - Moved into d(DamageSource, float)
            f = f1 / 25.0F;
        }

        return f;
    }

    protected float applyMagicModifier(DamageSource damagesource, float f) {
        if (damagesource.isStarvation()) {
            return f;
        } else {
            int i;
            int j;
            float f1;
               
            // CraftBukkit - Moved to d(DamageSource, float)
            if (false && this.hasEffect(MobEffectList.RESISTANCE) && damagesource != DamageSource.OUT_OF_WORLD) {
                i = (this.getEffect(MobEffectList.RESISTANCE).getAmplifier() + 1) * 5;
                j = 25 - i;
                f1 = f * (float) j;
                f = f1 / 25.0F;
            }

            if (f <= 0.0F) {
                return 0.0F;
            } else {
                i = EnchantmentManager.a(this.getEquipment(), damagesource);
                if (i > 20) {
                    i = 20;
                }

                if (i > 0 && i <= 20) {
                    j = 25 - i;
                    f1 = f * (float) j;
                    f = f1 / 25.0F;
                }

                return f;
            }
        }
    }

    // CraftBukkit start
    protected boolean d(final DamageSource damagesource, float f) { // void -> boolean, add final
        if (!this.isInvulnerable(damagesource)) {
            final boolean human = this instanceof EntityHuman;
            float originalDamage = f;
            Function<Double, Double> hardHat = new Function<Double, Double>() {
                @Override
                public Double apply(Double f) {
                    if ((damagesource == DamageSource.ANVIL || damagesource == DamageSource.FALLING_BLOCK) && EntityLiving.this.getEquipment(4) != null) {
                        return -(f - (f * 0.75F));
                    }
                    return -0.0;
                }
            };
            float hardHatModifier = hardHat.apply((double) f).floatValue();
            f += hardHatModifier;

            Function<Double, Double> blocking = new Function<Double, Double>() {
                @Override
                public Double apply(Double f) {
                    if (human) {
                        if (!damagesource.ignoresArmor() && ((EntityHuman) EntityLiving.this).isBlocking() && f > 0.0F) {
                            return -(f - ((1.0F + f) * 0.5F));
                        }
                    }
                    return -0.0;
                }
            };
            float blockingModifier = blocking.apply((double) f).floatValue();
            f += blockingModifier;

            Function<Double, Double> armor = new Function<Double, Double>() {
                @Override
                public Double apply(Double f) {
                    return -(f - EntityLiving.this.applyArmorModifier(damagesource, f.floatValue()));
                }
            };
            float armorModifier = armor.apply((double) f).floatValue();
            f += armorModifier;

            Function<Double, Double> resistance = new Function<Double, Double>() {
                @Override
                public Double apply(Double f) {
                    if (!damagesource.isStarvation() && EntityLiving.this.hasEffect(MobEffectList.RESISTANCE) && damagesource != DamageSource.OUT_OF_WORLD) {
                        int i = (EntityLiving.this.getEffect(MobEffectList.RESISTANCE).getAmplifier() + 1) * 5;
                        int j = 25 - i;
                        float f1 = f.floatValue() * (float) j;
                        return -(f - (f1 / 25.0F));
                    }
                    return -0.0;
                }
            };
            float resistanceModifier = resistance.apply((double) f).floatValue();
            f += resistanceModifier;

            Function<Double, Double> magic = new Function<Double, Double>() {
                @Override
                public Double apply(Double f) {
                    return -(f - EntityLiving.this.applyMagicModifier(damagesource, f.floatValue()));
                }
            };
            float magicModifier = magic.apply((double) f).floatValue();
            f += magicModifier;

            Function<Double, Double> absorption = new Function<Double, Double>() {
                @Override
                public Double apply(Double f) {
                    return -(Math.max(f - Math.max(f - EntityLiving.this.getAbsorptionHearts(), 0.0F), 0.0F));
                }
            };
            float absorptionModifier = absorption.apply((double) f).floatValue();

            EntityDamageEvent event = CraftEventFactory.handleLivingEntityDamageEvent(this, damagesource, originalDamage, hardHatModifier, blockingModifier, armorModifier, resistanceModifier, magicModifier, absorptionModifier, hardHat, blocking, armor, resistance, magic, absorption);
            if (event.isCancelled()) {
                return false;
            }

            f = (float) event.getFinalDamage();

            // Apply damage to helmet
            if ((damagesource == DamageSource.ANVIL || damagesource == DamageSource.FALLING_BLOCK) && this.getEquipment(4) != null) {
                this.getEquipment(4).damage((int) (event.getDamage() * 4.0F + this.random.nextFloat() * event.getDamage() * 2.0F), this);
            }
            
            // Apply damage to armor
            if (!damagesource.ignoresArmor()) {
                float armorDamage = (float) (event.getDamage() + event.getDamage(DamageModifier.BLOCKING) + event.getDamage(DamageModifier.HARD_HAT));
                this.damageArmor(armorDamage);
            }

            absorptionModifier = (float) -event.getDamage(DamageModifier.ABSORPTION);
            this.setAbsorptionHearts(Math.max(this.getAbsorptionHearts() - absorptionModifier, 0.0F));
            if (f != 0.0F) {
                if (human) {
                    ((EntityHuman) this).applyExhaustion(damagesource.getExhaustionCost());
                }
                // CraftBukkit end
                float f2 = this.getHealth();

                this.setHealth(f2 - f);
                this.br().a(damagesource, f2, f);
                // CraftBukkit start
                if (human) {
                    return true;
                }
                // CraftBukkit end
                this.setAbsorptionHearts(this.getAbsorptionHearts() - f);
            }
            return true; // CraftBukkit
        }
        return false; // CraftBukkit
    }

    public CombatTracker br() {
        return this.combatTracker;
    }

    public EntityLiving bs() {
        return (EntityLiving) (this.combatTracker.c() != null ? this.combatTracker.c() : (this.killer != null ? this.killer : (this.lastDamager != null ? this.lastDamager : null)));
    }

    public final float getMaxHealth() {
        return (float) this.getAttributeInstance(GenericAttributes.maxHealth).getValue();
    }

    public final int bu() {
        return this.datawatcher.getByte(9);
    }

    public final void o(int i) {
        this.datawatcher.watch(9, Byte.valueOf((byte) i));
    }

    private int n() {
        return this.hasEffect(MobEffectList.FASTER_DIG) ? 6 - (1 + this.getEffect(MobEffectList.FASTER_DIG).getAmplifier()) * 1 : (this.hasEffect(MobEffectList.SLOWER_DIG) ? 6 + (1 + this.getEffect(MobEffectList.SLOWER_DIG).getAmplifier()) * 2 : 6);
    }

    public void bv() {
        if (!this.ap || this.aq >= this.n() / 2 || this.aq < 0) {
            this.aq = -1;
            this.ap = true;
            if (this.world instanceof WorldServer) {
                ((WorldServer) this.world).getTracker().a((Entity) this, (Packet) (new PacketPlayOutAnimation(this, 0)));
            }
        }

    }

    protected void O() {
        this.damageEntity(DamageSource.OUT_OF_WORLD, 4.0F);
    }

    protected void bw() {
        int i = this.n();

        if (this.ap) {
            ++this.aq;
            if (this.aq >= i) {
                this.aq = 0;
                this.ap = false;
            }
        } else {
            this.aq = 0;
        }

        this.ax = (float) this.aq / (float) i;
    }

    public AttributeInstance getAttributeInstance(IAttribute iattribute) {
        return this.getAttributeMap().a(iattribute);
    }

    public AttributeMapBase getAttributeMap() {
        if (this.c == null) {
            this.c = new AttributeMapServer();
        }

        return this.c;
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEFINED;
    }

    public abstract ItemStack bz();

    public abstract ItemStack getEquipment(int i);

    public abstract void setEquipment(int i, ItemStack itemstack);

    public void setSprinting(boolean flag) {
        super.setSprinting(flag);
        AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.d);

        if (attributeinstance.a(EntityLiving.a) != null) {
            attributeinstance.c(EntityLiving.b);
        }

        if (flag) {
            attributeinstance.b(EntityLiving.b);
        }

    }

    public abstract ItemStack[] getEquipment();

    protected float bA() {
        return 1.0F;
    }

    protected float bB() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    protected boolean bC() {
        return this.getHealth() <= 0.0F;
    }

    public void q(Entity entity) {
        double d0 = entity.locX;
        double d1 = entity.getBoundingBox().b + (double) entity.length;
        double d2 = entity.locZ;
        byte b0 = 1;

        for (int i = -b0; i <= b0; ++i) {
            for (int j = -b0; j < b0; ++j) {
                if (i != 0 || j != 0) {
                    int k = (int) (this.locX + (double) i);
                    int l = (int) (this.locZ + (double) j);
                    AxisAlignedBB axisalignedbb = this.getBoundingBox().c((double) i, 1.0D, (double) j);

                    if (this.world.a(axisalignedbb).isEmpty()) {
                        if (World.a((IBlockAccess) this.world, new BlockPosition(k, (int) this.locY, l))) {
                            this.enderTeleportTo(this.locX + (double) i, this.locY + 1.0D, this.locZ + (double) j);
                            return;
                        }

                        if (World.a((IBlockAccess) this.world, new BlockPosition(k, (int) this.locY - 1, l)) || this.world.getType(new BlockPosition(k, (int) this.locY - 1, l)).getBlock().getMaterial() == Material.WATER) {
                            d0 = this.locX + (double) i;
                            d1 = this.locY + 1.0D;
                            d2 = this.locZ + (double) j;
                        }
                    }
                }
            }
        }

        this.enderTeleportTo(d0, d1, d2);
    }

    protected float bD() {
        return 0.42F;
    }

    protected void bE() {
        this.motY = (double) this.bD();
        if (this.hasEffect(MobEffectList.JUMP)) {
            this.motY += (double) ((float) (this.getEffect(MobEffectList.JUMP).getAmplifier() + 1) * 0.1F);
        }

        if (this.isSprinting()) {
            float f = this.yaw * 0.017453292F;

            this.motX -= (double) (MathHelper.sin(f) * 0.2F);
            this.motZ += (double) (MathHelper.cos(f) * 0.2F);
        }

        this.ai = true;
    }

    protected void bF() {
        this.motY += 0.03999999910593033D;
    }

    protected void bG() {
        this.motY += 0.03999999910593033D;
    }

    public void g(float f, float f1) {
        double d0;
        float f2;

        if (this.bL()) {
            float f3;
            float f4;

            if (this.V() && (!(this instanceof EntityHuman) || !((EntityHuman) this).abilities.isFlying)) {
                d0 = this.locY;
                f3 = 0.8F;
                f4 = 0.02F;
                f2 = (float) EnchantmentManager.b(this);
                if (f2 > 3.0F) {
                    f2 = 3.0F;
                }

                if (!this.onGround) {
                    f2 *= 0.5F;
                }

                if (f2 > 0.0F) {
                    f3 += (0.54600006F - f3) * f2 / 3.0F;
                    f4 += (this.bH() * 1.0F - f4) * f2 / 3.0F;
                }

                this.a(f, f1, f4);
                this.move(this.motX, this.motY, this.motZ);
                this.motX *= (double) f3;
                this.motY *= 0.800000011920929D;
                this.motZ *= (double) f3;
                this.motY -= 0.02D;
                if (this.positionChanged && this.c(this.motX, this.motY + 0.6000000238418579D - this.locY + d0, this.motZ)) {
                    this.motY = 0.30000001192092896D;
                }
            } else if (this.ab() && (!(this instanceof EntityHuman) || !((EntityHuman) this).abilities.isFlying)) {
                d0 = this.locY;
                this.a(f, f1, 0.02F);
                this.move(this.motX, this.motY, this.motZ);
                this.motX *= 0.5D;
                this.motY *= 0.5D;
                this.motZ *= 0.5D;
                this.motY -= 0.02D;
                if (this.positionChanged && this.c(this.motX, this.motY + 0.6000000238418579D - this.locY + d0, this.motZ)) {
                    this.motY = 0.30000001192092896D;
                }
            } else {
                float f5 = 0.91F;

                if (this.onGround) {
                    f5 = this.world.getType(new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.getBoundingBox().b) - 1, MathHelper.floor(this.locZ))).getBlock().frictionFactor * 0.91F;
                }

                float f6 = 0.16277136F / (f5 * f5 * f5);

                if (this.onGround) {
                    f3 = this.bH() * f6;
                } else {
                    f3 = this.aK;
                }

                this.a(f, f1, f3);
                f5 = 0.91F;
                if (this.onGround) {
                    f5 = this.world.getType(new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.getBoundingBox().b) - 1, MathHelper.floor(this.locZ))).getBlock().frictionFactor * 0.91F;
                }

                if (this.j_()) {
                    f4 = 0.15F;
                    this.motX = MathHelper.a(this.motX, (double) (-f4), (double) f4);
                    this.motZ = MathHelper.a(this.motZ, (double) (-f4), (double) f4);
                    this.fallDistance = 0.0F;
                    if (this.motY < -0.15D) {
                        this.motY = -0.15D;
                    }

                    boolean flag = this.isSneaking() && this instanceof EntityHuman;

                    if (flag && this.motY < 0.0D) {
                        this.motY = 0.0D;
                    }
                }

                this.move(this.motX, this.motY, this.motZ);
                if (this.positionChanged && this.j_()) {
                    this.motY = 0.2D;
                }

                if (this.world.isStatic && (!this.world.isLoaded(new BlockPosition((int) this.locX, 0, (int) this.locZ)) || !this.world.getChunkAtWorldCoords(new BlockPosition((int) this.locX, 0, (int) this.locZ)).o())) {
                    if (this.locY > 0.0D) {
                        this.motY = -0.1D;
                    } else {
                        this.motY = 0.0D;
                    }
                } else {
                    this.motY -= 0.08D;
                }

                this.motY *= 0.9800000190734863D;
                this.motX *= (double) f5;
                this.motZ *= (double) f5;
            }
        }

        this.ay = this.az;
        d0 = this.locX - this.lastX;
        double d1 = this.locZ - this.lastZ;

        f2 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        this.az += (f2 - this.az) * 0.4F;
        this.aA += this.az;
    }

    public float bH() {
        return this.bk;
    }

    public void j(float f) {
        this.bk = f;
    }

    public boolean r(Entity entity) {
        this.p(entity);
        return false;
    }

    public boolean isSleeping() {
        return false;
    }

    public void s_() {
        SpigotTimings.timerEntityBaseTick.startTiming(); // Spigot
        super.s_();
        if (!this.world.isStatic) {
            int i = this.bu();

            if (i > 0) {
                if (this.ar <= 0) {
                    this.ar = 20 * (30 - i);
                }

                --this.ar;
                if (this.ar <= 0) {
                    this.o(i - 1);
                }
            }

            for (int j = 0; j < 5; ++j) {
                ItemStack itemstack = this.h[j];
                ItemStack itemstack1 = this.getEquipment(j);

                if (!ItemStack.matches(itemstack1, itemstack)) {
                    ((WorldServer) this.world).getTracker().a((Entity) this, (Packet) (new PacketPlayOutEntityEquipment(this.getId(), j, itemstack1)));
                    if (itemstack != null) {
                        this.c.a(itemstack.B());
                    }

                    if (itemstack1 != null) {
                        this.c.b(itemstack1.B());
                    }

                    this.h[j] = itemstack1 == null ? null : itemstack1.cloneItemStack();
                }
            }

            if (this.ticksLived % 20 == 0) {
                this.br().g();
            }
        }

        SpigotTimings.timerEntityBaseTick.stopTiming(); // Spigot
        this.m();
        SpigotTimings.timerEntityTickRest.startTiming(); // Spigot
        double d0 = this.locX - this.lastX;
        double d1 = this.locZ - this.lastZ;
        float f = (float) (d0 * d0 + d1 * d1);
        float f1 = this.aG;
        float f2 = 0.0F;

        this.aP = this.aQ;
        float f3 = 0.0F;

        if (f > 0.0025000002F) {
            f3 = 1.0F;
            f2 = (float) Math.sqrt((double) f) * 3.0F;
            // CraftBukkit - Math -> TrigMath
            f1 = (float) org.bukkit.craftbukkit.TrigMath.atan2(d1, d0) * 180.0F / 3.1415927F - 90.0F;
        }

        if (this.ax > 0.0F) {
            f1 = this.yaw;
        }

        if (!this.onGround) {
            f3 = 0.0F;
        }

        this.aQ += (f3 - this.aQ) * 0.3F;
        this.world.methodProfiler.a("headTurn");
        f2 = this.h(f1, f2);
        this.world.methodProfiler.b();
        this.world.methodProfiler.a("rangeChecks");

        while (this.yaw - this.lastYaw < -180.0F) {
            this.lastYaw -= 360.0F;
        }

        while (this.yaw - this.lastYaw >= 180.0F) {
            this.lastYaw += 360.0F;
        }

        while (this.aG - this.aH < -180.0F) {
            this.aH -= 360.0F;
        }

        while (this.aG - this.aH >= 180.0F) {
            this.aH += 360.0F;
        }

        while (this.pitch - this.lastPitch < -180.0F) {
            this.lastPitch -= 360.0F;
        }

        while (this.pitch - this.lastPitch >= 180.0F) {
            this.lastPitch += 360.0F;
        }

        while (this.aI - this.aJ < -180.0F) {
            this.aJ -= 360.0F;
        }

        while (this.aI - this.aJ >= 180.0F) {
            this.aJ += 360.0F;
        }

        this.world.methodProfiler.b();
        this.aR += f2;
        SpigotTimings.timerEntityTickRest.stopTiming(); // Spigot
    }

    protected float h(float f, float f1) {
        float f2 = MathHelper.g(f - this.aG);

        this.aG += f2 * 0.3F;
        float f3 = MathHelper.g(this.yaw - this.aG);
        boolean flag = f3 < -90.0F || f3 >= 90.0F;

        if (f3 < -75.0F) {
            f3 = -75.0F;
        }

        if (f3 >= 75.0F) {
            f3 = 75.0F;
        }

        this.aG = this.yaw - f3;
        if (f3 * f3 > 2500.0F) {
            this.aG += f3 * 0.2F;
        }

        if (flag) {
            f1 *= -1.0F;
        }

        return f1;
    }

    public void m() {
        if (this.bl > 0) {
            --this.bl;
        }

        if (this.ba > 0) {
            double d0 = this.locX + (this.bb - this.locX) / (double) this.ba;
            double d1 = this.locY + (this.bc - this.locY) / (double) this.ba;
            double d2 = this.locZ + (this.bd - this.locZ) / (double) this.ba;
            double d3 = MathHelper.g(this.be - (double) this.yaw);

            this.yaw = (float) ((double) this.yaw + d3 / (double) this.ba);
            this.pitch = (float) ((double) this.pitch + (this.bf - (double) this.pitch) / (double) this.ba);
            --this.ba;
            this.setPosition(d0, d1, d2);
            this.setYawPitch(this.yaw, this.pitch);
        } else if (!this.bL()) {
            this.motX *= 0.98D;
            this.motY *= 0.98D;
            this.motZ *= 0.98D;
        }

        if (Math.abs(this.motX) < 0.005D) {
            this.motX = 0.0D;
        }

        if (Math.abs(this.motY) < 0.005D) {
            this.motY = 0.0D;
        }

        if (Math.abs(this.motZ) < 0.005D) {
            this.motZ = 0.0D;
        }

        this.world.methodProfiler.a("ai");
        SpigotTimings.timerEntityAI.startTiming(); // Spigot
        if (this.bC()) {
            this.aW = false;
            this.aX = 0.0F;
            this.aY = 0.0F;
            this.aZ = 0.0F;
        } else if (this.bL()) {
            this.world.methodProfiler.a("newAi");
            this.doTick();
            this.world.methodProfiler.b();
        }
        SpigotTimings.timerEntityAI.stopTiming(); // Spigot

        this.world.methodProfiler.b();
        this.world.methodProfiler.a("jump");
        if (this.aW) {
            if (this.V()) {
                this.bF();
            } else if (this.ab()) {
                this.bG();
            } else if (this.onGround && this.bl == 0) {
                this.bE();
                this.bl = 10;
            }
        } else {
            this.bl = 0;
        }

        this.world.methodProfiler.b();
        this.world.methodProfiler.a("travel");
        this.aX *= 0.98F;
        this.aY *= 0.98F;
        this.aZ *= 0.9F;
        SpigotTimings.timerEntityAIMove.startTiming(); // Spigot
        this.g(this.aX, this.aY);
        SpigotTimings.timerEntityAIMove.stopTiming(); // Spigot
        this.world.methodProfiler.b();
        this.world.methodProfiler.a("push");
        if (!this.world.isStatic) {
            SpigotTimings.timerEntityAICollision.startTiming(); // Spigot
            this.bK();
            SpigotTimings.timerEntityAICollision.stopTiming(); // Spigot
        }

        this.world.methodProfiler.b();
    }

    protected void doTick() {}

    protected void bK() {
        List list = this.world.getEntities(this, this.getBoundingBox().grow(0.20000000298023224D, 0.0D, 0.20000000298023224D));

        if (this.ad() && list != null && !list.isEmpty()) { // Spigot: Add this.ad() condition
            numCollisions -= world.spigotConfig.maxCollisionsPerEntity; // Spigot
            for (int i = 0; i < list.size(); ++i) {
                if (numCollisions > world.spigotConfig.maxCollisionsPerEntity) { break; } // Spigot
                Entity entity = (Entity) list.get(i);
                
                // TODO better check now?
                // CraftBukkit start - Only handle mob (non-player) collisions every other tick
                if (entity instanceof EntityLiving && !(this instanceof EntityPlayer) && this.ticksLived % 2 == 0) {
                    continue;
                }
                // CraftBukkit end

                if (entity.ae()) {
                    entity.numCollisions++; // Spigot
                    numCollisions++; // Spigot
                    this.s(entity);
                }
            }
            numCollisions = 0; // Spigot
        }

    }

    protected void s(Entity entity) {
        entity.collide(this);
    }

    public void mount(Entity entity) {
        if (this.vehicle != null && entity == null) {
            // CraftBukkit start
            Entity originalVehicle = this.vehicle;
            if ((this.bukkitEntity instanceof LivingEntity) && (this.vehicle.getBukkitEntity() instanceof Vehicle)) {
                VehicleExitEvent event = new VehicleExitEvent((Vehicle) this.vehicle.getBukkitEntity(), (LivingEntity) this.bukkitEntity);
                getBukkitEntity().getServer().getPluginManager().callEvent(event);

                if (event.isCancelled() || vehicle != originalVehicle) {
                    return;
                }
            }
            // CraftBukkit end

            if (!this.world.isStatic) {
                this.q(this.vehicle);
            }

            if (this.vehicle != null) {
                this.vehicle.passenger = null;
            }

            this.vehicle = null;
        } else {
            super.mount(entity);
        }
    }

    public void ak() {
        super.ak();
        this.aP = this.aQ;
        this.aQ = 0.0F;
        this.fallDistance = 0.0F;
    }

    public void i(boolean flag) {
        this.aW = flag;
    }

    public void receive(Entity entity, int i) {
        if (!entity.dead && !this.world.isStatic) {
            EntityTracker entitytracker = ((WorldServer) this.world).getTracker();

            if (entity instanceof EntityItem) {
                entitytracker.a(entity, (Packet) (new PacketPlayOutCollect(entity.getId(), this.getId())));
            }

            if (entity instanceof EntityArrow) {
                entitytracker.a(entity, (Packet) (new PacketPlayOutCollect(entity.getId(), this.getId())));
            }

            if (entity instanceof EntityExperienceOrb) {
                entitytracker.a(entity, (Packet) (new PacketPlayOutCollect(entity.getId(), this.getId())));
            }
        }

    }

    public boolean hasLineOfSight(Entity entity) {
        return this.world.rayTrace(new Vec3D(this.locX, this.locY + (double) this.getHeadHeight(), this.locZ), new Vec3D(entity.locX, entity.locY + (double) entity.getHeadHeight(), entity.locZ)) == null;
    }

    public Vec3D ap() {
        return this.d(1.0F);
    }

    public Vec3D d(float f) {
        if (f == 1.0F) {
            return this.f(this.pitch, this.aI);
        } else {
            float f1 = this.lastPitch + (this.pitch - this.lastPitch) * f;
            float f2 = this.aJ + (this.aI - this.aJ) * f;

            return this.f(f1, f2);
        }
    }

    public boolean bL() {
        return !this.world.isStatic;
    }

    public boolean ad() {
        return !this.dead;
    }

    public boolean ae() {
        return !this.dead;
    }

    protected void ac() {
        this.velocityChanged = this.random.nextDouble() >= this.getAttributeInstance(GenericAttributes.c).getValue();
    }

    public float getHeadRotation() {
        return this.aI;
    }

    public void f(float f) {
        this.aI = f;
    }

    public float getAbsorptionHearts() {
        return this.bm;
    }

    public void setAbsorptionHearts(float f) {
        if (f < 0.0F) {
            f = 0.0F;
        }

        this.bm = f;
    }

    public ScoreboardTeamBase getScoreboardTeam() {
        return this.world.getScoreboard().getPlayerTeam(this.getUniqueID().toString());
    }

    public boolean c(EntityLiving entityliving) {
        return this.a(entityliving.getScoreboardTeam());
    }

    public boolean a(ScoreboardTeamBase scoreboardteambase) {
        return this.getScoreboardTeam() != null ? this.getScoreboardTeam().isAlly(scoreboardteambase) : false;
    }

    public void enterCombat() {}

    public void exitCombat() {}

    protected void bO() {
        this.updateEffects = true;
    }
}